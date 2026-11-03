package dev.tberghuis.voicememos.vm

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import dev.tberghuis.voicememos.common.deleteFileCommon
import dev.tberghuis.voicememos.media.WrSimplePlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi
class PrivateRecordingsVmc(
  private val context: Context,
  private val scope: CoroutineScope,
  private val player: WrSimplePlayer,
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

  fun playRecording(file: File) {
    scope.launch {
      player.play(file.toUri())
    }
  }
}