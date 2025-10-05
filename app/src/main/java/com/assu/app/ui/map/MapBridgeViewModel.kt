package com.assu.app.ui.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed interface MapEvent {
    data class ShowContract(
        val partnershipId: Long,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val partnerName: String? = null,
        val adminName: String? = null,
        val term : String? = null,
        val profileUrl : String? = null,
        val phoneNum : String? = null
    ) : MapEvent
}

class MapBridgeViewModel : ViewModel() {
    private val _events = MutableSharedFlow<MapEvent>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<MapEvent> = _events

    fun showContract(
        partnershipId: Long,
        latitude: Double? = null,
        longitude: Double? = null,
        partnerName: String? = null,
        adminName: String? = null,
        term: String? = null,
        profileUrl: String? = null,
        phoneNum: String? = null
    ) {
        _events.tryEmit(MapEvent.ShowContract(partnershipId, latitude, longitude, partnerName, adminName, term, profileUrl))
    }
}