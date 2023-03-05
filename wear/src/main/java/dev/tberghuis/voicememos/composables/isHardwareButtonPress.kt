package dev.tberghuis.voicememos.composables

import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_STEM_1
import androidx.compose.ui.input.key.KeyEvent
import dev.tberghuis.voicememos.util.logd


//  val isHardwareButtonPress = { keyEvent: KeyEvent ->
//    if (keyEvent.nativeKeyEvent.action == ACTION_UP && keyEvent.nativeKeyEvent.keyCode == KEYCODE_STEM_1) {
//      logd("physical key")
//      true
//    } else {
//      false
//    }
//  }


fun isHardwareButtonPress(keyEvent: KeyEvent): Boolean {
  if (keyEvent.nativeKeyEvent.action == ACTION_UP && keyEvent.nativeKeyEvent.keyCode == KEYCODE_STEM_1) {
    logd("physical key")
    return true
  }
  return false
}



