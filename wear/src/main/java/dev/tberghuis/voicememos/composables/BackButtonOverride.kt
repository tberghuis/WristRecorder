package dev.tberghuis.voicememos.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.activity.BackEventCompat
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.data.settingsRepository
import dev.tberghuis.voicememos.viewmodels.RecordingUiViewModel
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow

@Composable
fun BackButtonOverride(
  vm: RecordingUiViewModel = viewModel(
    viewModelStoreOwner
    = LocalActivity.current as ViewModelStoreOwner
  )
) {
  val activity = LocalActivity.current
  val backOverrideSetting =
    LocalContext.current.settingsRepository.backOverrideFlow().collectAsState(false).value

  PredictiveBackHandler { progress: Flow<BackEventCompat> ->
    var isGesture = false
    try {
      progress.collect { backEvent ->
        logd("PredictiveBackHandler backEvent")
        isGesture = true
      }
      logd("PredictiveBackHandler complete")
      if (isGesture || !backOverrideSetting) {
        activity?.finish()
      } else {
        // this should only happen if physical back key pressed and backOverrideSetting=true
        vm.toggleRecording()
      }
    } catch (e: CancellationException) {
      logd("PredictiveBackHandler cancelled")
      throw e
    } finally {
      logd("PredictiveBackHandler finally")
    }
  }
}