package dev.tberghuis.voicememos.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.tberghuis.voicememos.service.RecordingService
import dev.tberghuis.voicememos.service.RecordingServiceManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class RecordingUiViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private val recordingServiceManager = RecordingServiceManager(application)

  private val recordingService: RecordingService?
    get() = recordingServiceManager.recordingServiceFlow.value

  var isRecording by mutableStateOf(false)
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

  override fun onCleared() {
    if (!isRecording) {
      recordingService?.stopSelf()
    }
    super.onCleared()
  }
}