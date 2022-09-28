package dev.tberghuis.voicememos.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import dev.tberghuis.voicememos.service.AudioConstants.Companion.CHANNELS_OUT
import dev.tberghuis.voicememos.service.AudioConstants.Companion.RECORDING_RATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext



// todo rewrite so i can play/pause

class AudioTrackService(val context: Context) {


  init {
  }

  suspend fun play(filename: String) {
    val intSize = AudioTrack.getMinBufferSize(RECORDING_RATE, CHANNELS_OUT, AudioConstants.FORMAT)

    val audioTrack = AudioTrack.Builder().setAudioAttributes(
      AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .setUsage(AudioAttributes.USAGE_MEDIA).build()
    ).setBufferSizeInBytes(intSize).setAudioFormat(
      AudioFormat.Builder().setSampleRate(RECORDING_RATE).setChannelMask(CHANNELS_OUT)
        .setEncoding(AudioConstants.FORMAT).build()
    ).setTransferMode(AudioTrack.MODE_STREAM).build()

    audioTrack.setVolume(AudioTrack.getMaxVolume())
    audioTrack.play()

    try {
      withContext(Dispatchers.IO) {
        context.openFileInput(filename).buffered().use { bufferedInputStream ->
          val buffer = ByteArray(intSize * 2)
          while (isActive) {
            val read = bufferedInputStream.read(buffer, 0, buffer.size)
            if (read < 0) break
            audioTrack.write(buffer, 0, read)
          }
        }
      }
    } finally {
      audioTrack.release()
    }
  }

}