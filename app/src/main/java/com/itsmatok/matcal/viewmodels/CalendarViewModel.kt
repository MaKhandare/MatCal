package com.itsmatok.matcal.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import biweekly.Biweekly
import biweekly.ICalendar
import com.itsmatok.matcal.R
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.itsmatok.matcal.data.calendar.events.CalendarEventDatabase
import com.itsmatok.matcal.data.calendar.events.EventMapper
import com.itsmatok.matcal.data.calendar.events.RecurrenceUtil
import com.itsmatok.matcal.data.calendar.subscriptions.CalendarSubscription
import com.itsmatok.matcal.notifications.EventNotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val db = CalendarEventDatabase.getDatabase(application)
    private val eventDao = db.eventDao()
    private val subDao = db.subscriptionDao()
    private val notificationScheduler = EventNotificationScheduler(application)

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val subscriptions: Flow<List<CalendarSubscription>> = subDao.getAllSubscriptionsFlow()

    val events: Flow<Map<LocalDate, List<CalendarEvent>>> =
        eventDao.getAllEvents()
            .map { list -> RecurrenceUtil.expandEvents(list) }
            .flowOn(Dispatchers.Default)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: Flow<List<CalendarEvent>> =
        searchQuery
            .map { it.trim() }
            .debounce(250)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    eventDao.searchEventsByTitle(query)
                }
            }


    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch {
            val insertedId = eventDao.insertEvent(event).toInt()
            notificationScheduler.schedule(event.copy(id = insertedId))
        }
    }

    fun getEventById(id: Int): Flow<CalendarEvent?> {
        return eventDao.getEventById(id)
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            notificationScheduler.cancel(id)
            eventDao.deleteEventById(id)
        }
    }

    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch {
            notificationScheduler.cancel(event.id)
            eventDao.updateEvent(event)
            notificationScheduler.schedule(event)
        }
    }

    fun deleteSubscription(subscription: CalendarSubscription) {
        viewModelScope.launch(Dispatchers.IO) {
            eventDao.getEventsByUrl(subscription.url).forEach { event ->
                notificationScheduler.cancel(event.id)
            }
            subDao.delete(subscription)
            eventDao.deleteEventsBySource(subscription.url)

            showToast(R.string.toast_removed_schedule, subscription.name)
        }
    }

    fun importEventsFromUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                if (subDao.getSubscriptionByUrl(url) != null) {
                    showToast(R.string.toast_schedule_already_imported)
                    return@launch
                }

                val iCalData = URL(url).readText()
                val iCal = Biweekly.parse(iCalData).first()

                if (iCal == null) {
                    showToast(R.string.toast_parse_calendar_failed)
                    return@launch
                }

                val calNameProperty = iCal.getExperimentalProperty("X-WR-CALNAME")
                val calendarName = calNameProperty?.value ?: getApplication<Application>()
                    .getString(R.string.default_schedule_name, url.takeLast(10))

                val newSub = CalendarSubscription(url = url, name = calendarName)
                subDao.insert(newSub)

                processAndSaveEvents(url, iCal, calendarName)
                showToast(R.string.toast_imported_schedule, calendarName)

            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = e.message?.takeIf { it.isNotBlank() }
                    ?: getApplication<Application>().getString(R.string.toast_unknown_error)
                showToast(R.string.toast_import_error, errorMessage)
            }
        }
    }


    fun refreshAllSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            val subs = subDao.getAllSubscriptions()
            if (subs.isEmpty()) {
                showToast(R.string.toast_no_schedules_to_refresh)
                return@launch
            }

            showToast(R.string.toast_refreshing)

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
        eventDao.getEventsByUrl(url).forEach { event ->
            notificationScheduler.cancel(event.id)
        }

        val eventsToSync = iCal.events.mapNotNull { vEvent ->
            EventMapper.mapVEventToCalendarEvent(vEvent, url, sourceName)
        }
        eventDao.syncEvents(url, eventsToSync)

        eventDao.getEventsByUrl(url).forEach { event ->
            notificationScheduler.schedule(event)
        }
    }

    private suspend fun showToast(@StringRes messageRes: Int, vararg formatArgs: Any) {
        withContext(Dispatchers.Main) {
            val message = getApplication<Application>().getString(messageRes, *formatArgs)
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
