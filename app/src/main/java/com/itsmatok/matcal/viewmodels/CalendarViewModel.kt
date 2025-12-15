package com.itsmatok.matcal.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import biweekly.Biweekly
import biweekly.ICalendar
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

    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch {
            eventDao.updateEvent(event)
        }
    }

    fun deleteSubscription(subscription: CalendarSubscription) {
        viewModelScope.launch(Dispatchers.IO) {
            subDao.delete(subscription)
            eventDao.deleteEventsBySource(subscription.url)

            showToast("Removed ${subscription.name}")
        }
    }

    fun importEventsFromUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                if (subDao.getSubscriptionByUrl(url) != null) {
                    showToast("This schedule is already imported!")
                    return@launch
                }

                val iCalData = URL(url).readText()
                val iCal = Biweekly.parse(iCalData).first()

                if (iCal == null) {
                    showToast("Failed to parse calendar data")
                    return@launch
                }

                val calNameProperty = iCal.getExperimentalProperty("X-WR-CALNAME")
                val calendarName = calNameProperty?.value ?: "Schedule ${url.takeLast(10)}"

                val newSub = CalendarSubscription(url = url, name = calendarName)
                subDao.insert(newSub)

                processAndSaveEvents(url, iCal, calendarName)
                showToast("Imported $calendarName")

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

            showToast("Refreshing...")

            subs.forEach { sub ->
                try {
                    val iCalData = URL(sub.url).readText()
                    val iCal = Biweekly.parse(iCalData).first()

                    if (iCal != null) {
                        processAndSaveEvents(sub.url, iCal, sub.name)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun processAndSaveEvents(url: String, iCal: ICalendar, sourceName: String) {
        val eventsToSync = iCal.events.mapNotNull { vEvent ->
            mapVEventToCalendarEvent(vEvent, url, sourceName)
        }
        eventDao.syncEvents(url, eventsToSync)
    }

    private fun mapVEventToCalendarEvent(
        vEvent: VEvent,
        url: String,
        calendarName: String
    ): CalendarEvent? {
        val startDate = vEvent.dateStart?.value ?: return null
        val endDate = vEvent.dateEnd?.value ?: vEvent.dateStart.value

        val zoneId = ZoneId.systemDefault()

        val localStartDate = startDate.toInstant().atZone(zoneId).toLocalDate()
        val localStartTime = startDate.toInstant().atZone(zoneId).toLocalTime()

        val localEndTime = endDate.toInstant().atZone(zoneId).toLocalTime()

        val summary = vEvent.summary?.value?.trim() ?: "No Title"
        val location = vEvent.location?.value?.trim()
        val description = vEvent.description?.value?.trim()
        val iCalUid = vEvent.uid?.value

        return CalendarEvent(
            date = localStartDate,
            startTime = localStartTime,
            endTime = localEndTime,
            title = summary.trim(),
            location = location,
            description = description,
            source = calendarName,
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