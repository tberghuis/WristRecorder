package dev.tberghuis.voicememos.util

import android.util.Log
import dev.tberghuis.voicememos.BuildConfig

fun logd(s: String) {
  if (BuildConfig.DEBUG)
    Log.d("xxx", s)
}