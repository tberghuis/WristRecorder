package dev.tberghuis.voicememos.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.tberghuis.voicememos.data.settingsRepository
import kotlinx.coroutines.launch

class SettingsVm(
  application: Application,
) : AndroidViewModel(application) {

  var backOverride by mutableStateOf(false)
  

  init {
    // doitwrong
    viewModelScope.launch {
      application.settingsRepository.backOverrideFlow().collect {
        backOverride = it
      }
    }
  }
}