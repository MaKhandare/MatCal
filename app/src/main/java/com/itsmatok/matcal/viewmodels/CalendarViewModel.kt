package com.itsmatok.matcal.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import biweekly.Biweekly
import biweekly.component.VEvent
import com.itsmatok.matcal.data.CalendarEvent
import com.itsmatok.matcal.data.CalendarEventDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val db = CalendarEventDatabase.getDatabase(application)
    private val dao = db.eventDao()

    val events: Flow<Map<LocalDate, List<CalendarEvent>>> =
        dao.getAllEvents().map { list -> list.groupBy { it.date } }

    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch {
            dao.insertEvent(event)
        }
    }

    fun getEventById(id: Int): Flow<CalendarEvent?> {
        return dao.getEventById(id)
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            dao.deleteEventById(id)
        }
    }

    fun importEventsFromUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("CalendarViewModel", "Importing events from URL: $url")

                val iCalData = URL(url).readText()
                val ical = Biweekly.parse(iCalData).first()

                if (ical == null) {
                    showToast("Failed to parse calendar data")
                    return@launch
                }

                var count = 0
                val eventsToSave = ical.events.mapNotNull { vEvent ->
                    mapVEventToCalendarEvent(vEvent)
                }

                eventsToSave.forEach { event ->
                    dao.insertEvent(event)
                    count++
                }

                showToast("Imported $count events successfully")

            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Error importing: ${e.message}")
            }
        }
    }

    private fun mapVEventToCalendarEvent(vEvent: VEvent): CalendarEvent? {
        val startDate = vEvent.dateStart?.value ?: return null
        val endDate = vEvent.dateEnd?.value ?: vEvent.dateStart.value

        val zoneId = ZoneId.systemDefault()

        val localStartDate = startDate.toInstant().atZone(zoneId).toLocalDate()
        val localStartTime = startDate.toInstant().atZone(zoneId).toLocalTime()

        val localEndTime = endDate.toInstant().atZone(zoneId).toLocalTime()

        val summary = vEvent.summary?.value ?: "No Title"
        val location = vEvent.location?.value
        val description = vEvent.description?.value

        return CalendarEvent(
            date = localStartDate,
            startTime = localStartTime,
            endTime = localEndTime,
            title = summary,
            location = location,
            description = description,
            source = "Imported"
        )
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }
}