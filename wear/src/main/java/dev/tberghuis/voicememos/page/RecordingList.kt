package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.responsive
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import dev.tberghuis.voicememos.HomeViewModel
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.formatTimestampFromFilename
import dev.tberghuis.voicememos.common.logd

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun RecordingList(
  onRecordingClick: (String) -> Unit
) {
  val viewModel: HomeViewModel = viewModel()
  val context = LocalContext.current

  if (viewModel.recordingFilesInitialised.value && viewModel.recordingFiles.value.isEmpty()) {
    Column(
      Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text("No recordings")
    }
    return
  }

  val columnState = rememberColumnState(responsive())

  ScreenScaffold(scrollState = columnState) {
    ScalingLazyColumn(
      columnState = columnState,
      modifier = Modifier.fillMaxSize(),
    ) {
      // this is to ensure i pass play store review:
      // Fits within the physical display area.
      // No text or controls are cut off by the screen edges.
      item {
        Spacer(Modifier.height(5.dp))
      }
      items(viewModel.recordingFiles.value.size) { i ->
        val file = viewModel.recordingFiles.value[i]
        val buttonText = remember(file, context) {
          "${formatTimestampFromFilename(file)} ${calcDuration(context, file)}s"
        }

        Chip(
          label = {
            Text(
              buttonText, Modifier.padding(horizontal = 5.dp),
              fontSize = 12.sp,
              overflow = TextOverflow.Ellipsis,
              maxLines = 1,
            )
          },
          onClick = {
            logd("play $file")
            onRecordingClick(file)
          },
          contentPadding = ChipDefaults.CompactChipTapTargetPadding,
        )
      }
    }
  }
}