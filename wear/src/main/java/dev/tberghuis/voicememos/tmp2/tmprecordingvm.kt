package dev.tberghuis.voicememos.tmp2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.tberghuis.voicememos.common.logd

class TmpRecordingVm(private val application: Application) : AndroidViewModel(application) {

  fun startRecording(){
    logd("TmpRecordingVm startRecording")
  }
}
