package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.MobileViewModel
import dev.tberghuis.voicememos.common.calcDuration
import java.io.File

@Composable
fun PrivateRecordingCard(
  recordingFile: File, vm: MobileViewModel = viewModel()
) {
  val context = LocalContext.current
  val durationSeconds = calcDuration(context, recordingFile.name)
  RecordingCard(
    filename = recordingFile.name,
    duration = durationSeconds,
    play = { vm.playRecording(recordingFile) },
    delete = { vm.deleteRecording(recordingFile) },
  )
}