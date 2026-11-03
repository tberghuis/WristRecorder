package dev.tberghuis.voicememos.composable

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import dev.tberghuis.voicememos.tmp.tmp01.MobileViewModel
import dev.tberghuis.voicememos.vm.SharedRecording

@OptIn(UnstableApi::class)
@Composable
fun SharedRecordingCard(
  sharedRecording: SharedRecording,
  vm: MobileViewModel = viewModel()
) {
  val isPlaying = vm.player.isPlayingUri == sharedRecording.uri
  RecordingCard(
    filename = sharedRecording.displayName,
    duration = sharedRecording.duration,
    play = { vm.sharedRecordingsVmc.playSharedRecording(sharedRecording) },
    delete = { vm.sharedRecordingsVmc.deleteSharedRecording(sharedRecording) },
    stop = { vm.player.stop() },
    isPlaying = isPlaying
  )
}