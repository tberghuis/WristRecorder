package dev.tberghuis.voicememos.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.service.RecordingService
import dev.tberghuis.voicememos.service.RecordingServiceManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecordingUiViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private val recordingServiceManager = RecordingServiceManager(application)

  private val recordingService: RecordingService?
    get() = recordingServiceManager.recordingServiceFlow.value

  var isRecording by mutableStateOf<Boolean?>(null)
    private set

  init {
    viewModelScope.launch {
      recordingServiceManager.recordingServiceFlow.filterNotNull().collect { service ->
        service.isRecordingFlow.collect {
          isRecording = it
        }
      }
    }
  }

  fun startRecording() {
    // doitwrong
    recordingService?.startRecording()
  }

  fun stopRecording(): String? {
    return recordingService?.stopRecording()
  }

  fun toggleRecording() {
    viewModelScope.launch {
      val isRecording = snapshotFlow {
        isRecording
      }.filterNotNull().first()
      logd("toggleRecording isRecording $isRecording")
      if (isRecording) {
        stopRecording()
      } else {
        startRecording()
      }
    }
  }

  override fun onCleared() {
    if (isRecording != true) {
      recordingService?.stopSelf()
    }
    super.onCleared()
  }
}