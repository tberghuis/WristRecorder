package dev.tberghuis.voicememos.screen

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tberghuis.voicememos.common.AudioController
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.service.DeleteFileService
import javax.inject.Inject

@HiltViewModel
class RecordingDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  val audioController: AudioController,
  @ApplicationContext val appContext: Context,
  val deleteFileService: DeleteFileService
) : ViewModel() {

  // file is navigation argument
  val file = savedStateHandle.get<String>("file")!!
  val showDeleteConfirm = mutableStateOf(false)
  val duration = calcDuration(appContext, file)
  init {
    logd("savedStateHandle $savedStateHandle")
  }
}