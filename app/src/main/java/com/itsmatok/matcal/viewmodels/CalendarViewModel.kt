package com.itsmatok.matcal.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itsmatok.matcal.data.CalendarEvent
import com.itsmatok.matcal.data.CalendarEventDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

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
}