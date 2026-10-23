package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.MobileViewModel

@Composable
fun SharedRecordingCard(
  sharedRecording: SharedRecording,
  vm: MobileViewModel = viewModel()
) {
  RecordingCard(
    filename = sharedRecording.displayName,
    duration = sharedRecording.duration,
    play = { vm.sharedRecordingsVmc.playSharedRecording(sharedRecording) },
    delete = { vm.sharedRecordingsVmc.deleteSharedRecording(sharedRecording) },
  )
}