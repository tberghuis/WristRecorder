package dev.tberghuis.voicememos.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Simple event bus for notifying when recordings have been updated.
 * Used by ProcessZipWorker to signal MobileViewModel to refresh the file list.
 */
object RecordingsEvent {
    private val _recordingsUpdated = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val recordingsUpdated = _recordingsUpdated.asSharedFlow()

    fun notifyRecordingsUpdated() {
        _recordingsUpdated.tryEmit(Unit)
    }
}
