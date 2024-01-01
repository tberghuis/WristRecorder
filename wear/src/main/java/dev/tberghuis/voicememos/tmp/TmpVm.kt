package dev.tberghuis.voicememos.tmp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.launch


class TmpVm(private val application: Application) : AndroidViewModel(application) {
  private val tmpServiceManager = TmpServiceManager(application)

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