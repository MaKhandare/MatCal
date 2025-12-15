package com.itsmatok.matcal.navigation

import kotlinx.serialization.Serializable

@Serializable
object Calendar

@Serializable
object License

@Serializable
object AddEvent

@Serializable
data class EditEvent(val eventId: Int)

@Serializable
data class EventDetails(val eventId: Int)

@Serializable
object ManageCalendars
