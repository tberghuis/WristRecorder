package dev.tberghuis.voicememos.service

import android.media.AudioFormat

class AudioConstants {

  companion object {
    const val RECORDING_RATE = 8000 // can go up to 44K, if needed
    const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
    const val CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO
    const val FORMAT = AudioFormat.ENCODING_PCM_16BIT
  }

}