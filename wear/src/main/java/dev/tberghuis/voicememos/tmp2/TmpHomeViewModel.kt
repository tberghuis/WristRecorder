package dev.tberghuis.voicememos.tmp2

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class TmpRecordingUiViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private val recordingServiceManager = TmpRecordingServiceManager(application)

  private val recordingService: TmpRecordingService?
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