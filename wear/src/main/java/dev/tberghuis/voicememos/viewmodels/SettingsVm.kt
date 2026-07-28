package dev.tberghuis.voicememos.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.tberghuis.voicememos.data.settingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsVm(
  application: Application,
) : AndroidViewModel(application) {
  val backOverride = MutableStateFlow(false)

  init {
    // doitwrong
    viewModelScope.launch {
      // is this better than 1 way data flow through repository?
      // https://developer.android.com/topic/libraries/architecture/datastore#compose-sample
      backOverride.value = application.settingsRepository.backOverrideFlow().first()
      backOverride.drop(1).collect {
        application.settingsRepository.updateBackOverride(it)
      }
    }
  }

  fun toggleBackOverride() {
    backOverride.update { 
      !it
    }
  }
}