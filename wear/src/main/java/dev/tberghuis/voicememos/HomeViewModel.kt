package dev.tberghuis.voicememos

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.wear.compose.foundation.pager.PagerState

class HomeViewModel(
  private val application: Application,
) : AndroidViewModel(application) {
  val recordingFiles = mutableStateOf(listOf<String>())
  var recordingFilesInitialised = mutableStateOf(false)

  val pageCount = 3
  val pagerState = PagerState(pageCount = { 3 })


  fun getRecordings() {
    val files = application.fileList().toList().filter { it.startsWith("wristrecorder_") }.sorted()
    recordingFiles.value = files
    recordingFilesInitialised.value = true
  }
}