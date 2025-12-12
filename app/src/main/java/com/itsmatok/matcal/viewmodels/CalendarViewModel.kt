package com.itsmatok.matcal.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import biweekly.Biweekly
import biweekly.component.VEvent
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.itsmatok.matcal.data.calendar.events.CalendarEventDatabase
import com.itsmatok.matcal.data.calendar.subscriptions.CalendarSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val db = CalendarEventDatabase.getDatabase(application)
    private val eventDao = db.eventDao()
    private val subDao = db.subscriptionDao()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()


    val events: Flow<Map<LocalDate, List<CalendarEvent>>> =
        eventDao.getAllEvents().map { list -> list.groupBy { it.date } }

    val subscriptions: Flow<List<CalendarSubscription>> = subDao.getAllSubscriptionsFlow()

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch {
            eventDao.insertEvent(event)
        }
    }

    fun getEventById(id: Int): Flow<CalendarEvent?> {
        return eventDao.getEventById(id)
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            eventDao.deleteEventById(id)
        }
    }

    fun deleteSubscription(subscription: CalendarSubscription) {
        viewModelScope.launch(Dispatchers.IO) {
            subDao.delete(subscription)
            eventDao.deleteEventsBySource(subscription.url)

            showToast("Removed '${subscription.name}'")
        }
    }

    fun importEventsFromUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                if (subDao.getSubscriptionByUrl(url) != null) {
                    showToast("This schedule is already imported!")
                    return@launch
                }

                showToast("Fetching schedule...")

                val name = "Schedule ${url.takeLast(10)}"

                val newSub = CalendarSubscription(url = url, name = name)
                subDao.insert(newSub)
                syncSingleUrl(url)

            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Error importing: ${e.message}")
            }
        }
    }

    fun refreshAllSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            val subs = subDao.getAllSubscriptions()
            if (subs.isEmpty()) {
                showToast("No schedules to refresh.")
                return@launch
            }

            var successCount = 0
            subs.forEach { sub ->
                try {
                    syncSingleUrl(sub.url)
                    successCount++
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            showToast("Refreshed $successCount schedules.")
        }
    }

    private suspend fun syncSingleUrl(url: String) {
        try {
            val iCalData = URL(url).readText()
            val iCal = Biweekly.parse(iCalData).first()

            if (iCal == null) {
                showToast("Failed to parse calendar data")
                return
            }

            val eventsToSync = iCal.events.mapNotNull { vEvent ->
                mapVEventToCalendarEvent(vEvent, url)
            }

            eventDao.syncEvents(url, eventsToSync)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun mapVEventToCalendarEvent(vEvent: VEvent, url: String): CalendarEvent? {
        val startDate = vEvent.dateStart?.value ?: return null
        val endDate = vEvent.dateEnd?.value ?: vEvent.dateStart.value

        val zoneId = ZoneId.systemDefault()

        val localStartDate = startDate.toInstant().atZone(zoneId).toLocalDate()
        val localStartTime = startDate.toInstant().atZone(zoneId).toLocalTime()

        val localEndTime = endDate.toInstant().atZone(zoneId).toLocalTime()

        val summary = vEvent.summary?.value ?: "No Title"
        val location = vEvent.location?.value
        val description = vEvent.description?.value
        val iCalUid = vEvent.uid?.value

        return CalendarEvent(
            date = localStartDate,
            startTime = localStartTime,
            endTime = localEndTime,
            title = summary,
            location = location,
            description = description,
            source = "Imported",
            sourceUrl = url,
            iCalUid = iCalUid
        )
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }
}