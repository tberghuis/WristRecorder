package dev.tberghuis.voicememos.page

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.tberghuis.voicememos.composables.RecordingUi
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.LocalNavController
import dev.tberghuis.voicememos.MainActivity
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.data.settingsRepository
import dev.tberghuis.voicememos.viewmodels.RecordingUiViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingPage(
  navigateRecordingDetail: (String) -> Unit
) {
  val context = LocalContext.current
  val permissionsList = if (Build.VERSION.SDK_INT >= 33) {
    listOf(
      android.Manifest.permission.RECORD_AUDIO,
      android.Manifest.permission.POST_NOTIFICATIONS,
    )
  } else {
    listOf(
      android.Manifest.permission.RECORD_AUDIO,
    )
  }
  val multiplePermissionsState = rememberMultiplePermissionsState(
    permissionsList
  )

  if (!multiplePermissionsState.allPermissionsGranted) {
    LaunchedEffect(Unit) {
      multiplePermissionsState.launchMultiplePermissionRequest()
    }
  }

  val recordPermissionState = multiplePermissionsState.permissions.find {
    it.permission == android.Manifest.permission.RECORD_AUDIO
  }!!

  BackButtonOverride()

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      Modifier
//      .fillMaxSize(0.8f),
        .fillMaxWidth(0.8f)
        .fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      when (recordPermissionState.status) {
        // user denied 0 or 2 times (prompt dialog dismissed when 0)
        PermissionStatus.Denied(false) -> {
          Text(
            "Please enable Microphone permission in settings",
            textAlign = TextAlign.Center,
          )
          Button(
            onClick = {
              launchPermissionsSettings(context)
            },
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text("Show Settings")
          }
        }

        PermissionStatus.Denied(true) -> {
          RecordingUi(navigateRecordingDetail) {
            recordPermissionState.launchPermissionRequest()
          }
        }

        PermissionStatus.Granted -> {
          RecordingUi(navigateRecordingDetail)
        }
        // redundant
        else -> {}
      }
    }
  }
}

fun launchPermissionsSettings(context: Context) {
  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
  val uri = Uri.fromParts("package", context.packageName, null)
  intent.setData(uri)
  context.startActivity(intent)
}

@Composable
fun BackButtonOverride(
  vm: RecordingUiViewModel = viewModel(
    viewModelStoreOwner
    = LocalActivity.current as ViewModelStoreOwner
  )
) {
  // backHandlerEnabled = true if physical key stem press && backoverride setting
  // this is wack but it works i guess
  val backButtonPressed = (LocalActivity.current as MainActivity).backButtonPressed.value
  val backOverrideSetting =
    LocalContext.current.settingsRepository.backOverrideFlow().collectAsState(false).value
  val navController = LocalNavController.current

  BackHandler(enabled = backButtonPressed && backOverrideSetting) {
    logd("recording page back handler")
    if (backButtonPressed && backOverrideSetting) {
      vm.toggleRecording()
    } else {
      navController.popBackStack()
    }
  }
}