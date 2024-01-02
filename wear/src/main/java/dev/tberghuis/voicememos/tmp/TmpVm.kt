package dev.tberghuis.voicememos.tmp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


class TmpVm(private val application: Application) : AndroidViewModel(application) {
  private val tmpServiceManager = TmpServiceManager(application)

  var isRecording by mutableStateOf(false)
    private set

  init {
    viewModelScope.launch {
      // this is wack
      tmpServiceManager.tmpServiceFlow.filterNotNull().collect { tmpService ->
        tmpService.isRecordingFlow.collect {
          isRecording = it
        }
      }
    }
  }

  fun tmpStartRecording() {
    viewModelScope.launch {
      tmpServiceManager.provideTmpService().startTmpWork()
    }
  }

  fun tmpGetCount() {
    viewModelScope.launch {
      val count = tmpServiceManager.provideTmpService().count
      logd("tmpGetCount count $count")
    }
  }

  fun tmpStopRecording() {
    viewModelScope.launch {
      tmpServiceManager.provideTmpService().stopTmpWork()
    }
  }

  override fun onCleared() {
    viewModelScope.launch {
      // todo if not recording
      tmpServiceManager.provideTmpService().stopSelf()
    }
    super.onCleared()
  }
}


//fun collectIntoStateFlow(
//  scope: CoroutineScope,
//  provideFlow: () -> StateFlow<Boolean>
//): StateFlow<Boolean> {
//  // doitwrong
//  val msf = MutableStateFlow(false)
//  scope.launch {
//    provideFlow().collect {
//      msf.value = it
//    }
//  }
//  return msf
//}
