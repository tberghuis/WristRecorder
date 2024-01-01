package dev.tberghuis.voicememos.tmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import dev.tberghuis.voicememos.common.logd

@Composable
fun TmpScreen(
  vm: TmpVm = viewModel()
) {

  ComposableLifecycle { _, event ->
    when (event) {
      Lifecycle.Event.ON_STOP -> {
        logd("On Stop")
        vm.unbind()
      }
      else -> {}
    }
  }




  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row {
      Button(onClick = {
        vm.tmpStartRecording()
      }) {
        Text("start")
      }
      Button(onClick = {
        vm.tmpGetCount()
      }) {
        Text("count")
      }
      Button(onClick = {
        vm.tmpStopRecording()
      }) {
        Text("stop")
      }
    }
    Row {
      Button(onClick = {
        vm.unbind()
      }) {
        Text("unbind")
      }
    }


  }
}


@Composable
fun ComposableLifecycle(
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
  onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { source, event ->
      onEvent(source, event)
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }
}



