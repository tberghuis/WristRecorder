package dev.tberghuis.voicememos.composable

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.vm.MobileViewModel
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun PrivateRecordingCard(
  recordingFile: File, vm: MobileViewModel = viewModel()
) {
  val context = LocalContext.current
  val durationSeconds = calcDuration(context, recordingFile.name)
  val isPlaying = vm.player.isPlayingUri == recordingFile.toUri()

  RecordingCard(
    filename = recordingFile.name,
    duration = durationSeconds,
    play = { vm.privateRecordingsVmc.playRecording(recordingFile) },
    delete = { vm.privateRecordingsVmc.deleteRecording(recordingFile) },
    stop = { vm.player.stop() },
    isPlaying = isPlaying
  )
}