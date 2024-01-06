package dev.tberghuis.voicememos

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class HomeViewModel(
  private val application: Application,
) : AndroidViewModel(application) {
  val recordingFiles = mutableStateOf(listOf<String>())
  var recordingFilesInitialised = mutableStateOf(false)

  fun getRecordings() {
    val files = application.fileList().toList().filter { it.startsWith("wristrecorder_") }.sorted()
    recordingFiles.value = files
    recordingFilesInitialised.value = true
  }
}