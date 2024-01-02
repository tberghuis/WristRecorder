package dev.tberghuis.voicememos

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dev.tberghuis.voicememos.common.AudioController

class HomeViewModel(
  private val application: Application,
  private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
  val recordingFiles = mutableStateOf(listOf<String>())
  var recordingFilesInitialised = mutableStateOf(false)
  val audioController = AudioController(application)

  fun getRecordings() {
    val files = application.fileList().toList().filter { it.startsWith("wristrecorder_") }.sorted()
    recordingFiles.value = files
    recordingFilesInitialised.value = true
  }
}