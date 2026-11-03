package dev.tberghuis.voicememos.tmp.tmp01

import android.content.Context
import dev.tberghuis.voicememos.common.deleteFileCommon
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class PrivateRecordingsVmc(
  private val context: Context,
) {


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
}