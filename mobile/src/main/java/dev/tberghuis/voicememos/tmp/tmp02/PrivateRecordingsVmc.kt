package dev.tberghuis.voicememos.tmp.tmp02

import android.content.Context
import dev.tberghuis.voicememos.common.AudioController
import dev.tberghuis.voicememos.common.deleteFileCommon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PrivateRecordingsVmc(
  private val context: Context,
  private val scope: CoroutineScope,
) {
  private val audioController = AudioController(context)

  val recordingFilesStateFlow = MutableStateFlow(listOf<File>())


  fun refreshPrivateRecordingFiles() {
    val path = context.filesDir
    val files = path.listFiles() ?: return
    val recordingFiles = mutableListOf<File>()
    files.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        recordingFiles.add(it)
      }
    }
    recordingFilesStateFlow.value = recordingFiles
  }

  fun deleteRecording(file: File) {
    deleteFileCommon(context, file.name)
    refreshPrivateRecordingFiles()
  }

  fun playRecording(file: File) {
    scope.launch {
      audioController.play(file.name)
    }
  }

}