package dev.tberghuis.voicememos

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tberghuis.voicememos.service.AudioRecordService
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  val audioRecordService: AudioRecordService,
  // suppressLint does not remove warning
  @ApplicationContext val appContext: Context,
) : ViewModel() {
  val recordingFiles = mutableStateOf(listOf<String>())
  var recordingFilesInitialised = mutableStateOf(false)

  fun getRecordings() {
    val files = appContext.fileList().toList().filter { it.startsWith("voicememo_") }
    recordingFiles.value = files
    recordingFilesInitialised.value = true
  }
}