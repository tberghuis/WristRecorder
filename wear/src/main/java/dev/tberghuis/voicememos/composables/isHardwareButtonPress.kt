package dev.tberghuis.voicememos.composables

import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_STEM_1
import androidx.compose.ui.input.key.KeyEvent
import dev.tberghuis.voicememos.common.logd

fun isHardwareButtonPress(keyEvent: KeyEvent): Boolean {
  if (keyEvent.nativeKeyEvent.action == ACTION_UP && keyEvent.nativeKeyEvent.keyCode == KEYCODE_STEM_1) {
    logd("physical key")
    return true
  }
  return false
}