package dev.tberghuis.voicememos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
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
  // suppresslint does not remove warning
  @ApplicationContext val appContext: Context,
) : ViewModel() {
  val recordPermissionGranted = mutableStateOf<Boolean?>(null)
  val recordingFiles = mutableStateOf(listOf<String>())
  var recordingFilesInitialised = mutableStateOf(false)

  init {
    checkPermission()
  }

  private fun checkPermission() {
    recordPermissionGranted.value = ContextCompat.checkSelfPermission(
      appContext, Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun getRecordings() {
    val files = appContext.fileList().toList().filter { it.startsWith("voicememo_") }
    recordingFiles.value = files
    recordingFilesInitialised.value = true
  }

}