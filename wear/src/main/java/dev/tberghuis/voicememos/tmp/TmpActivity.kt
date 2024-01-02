package dev.tberghuis.voicememos.tmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.tberghuis.voicememos.tmp2.TmpRecordingScreen

class TmpActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TmpRecordingScreen()
    }
  }
}

