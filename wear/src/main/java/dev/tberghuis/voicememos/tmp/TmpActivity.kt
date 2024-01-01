package dev.tberghuis.voicememos.tmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.voicememos.tmp2.TmpRecordingScreen

@AndroidEntryPoint
class TmpActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TmpRecordingScreen()
    }
  }
}

