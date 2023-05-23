package dev.tberghuis.voicememos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.tberghuis.voicememos.common.logd


class MessageViewModelTmp(private val application: Application) : AndroidViewModel(application) {
  fun willitblend() {
    logd("willitblend")
  }
}