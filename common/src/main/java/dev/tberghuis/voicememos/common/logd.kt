package dev.tberghuis.voicememos.common

import android.util.Log

fun logd(s: String) {
  // if (BuildConfig.DEBUG)
  // should be removed by proguard rules
  Log.d("xxx", s)
}