package dev.tberghuis.voicememos.screen

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.tberghuis.voicememos.common.AudioController
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.service.DeleteFileService


class RecordingDetailViewModel(
  private val application: Application,
  savedStateHandle: SavedStateHandle,

  ) : AndroidViewModel(application) {
  val audioController = AudioController(application)
  val deleteFileService = DeleteFileService(application)


  // file is navigation argument
  val file = savedStateHandle.get<String>("file")!!
  val showDeleteConfirm = mutableStateOf(false)
  val duration = calcDuration(application, file)

  init {
    logd("savedStateHandle $savedStateHandle")
  }
}









