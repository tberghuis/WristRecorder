package dev.tberghuis.voicememos.common

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext


class AudioController(val context: Context) {

  // https://github.com/android/wear-os-samples/blob/main/WearSpeakerSample/wear/src/main/java/com/example/android/wearable/speaker/SoundRecorder.kt
  suspend fun play(filename: String) {
    val intSize = AudioTrack.getMinBufferSize(RECORDING_RATE, CHANNELS_OUT, FORMAT)

    val audioTrack = AudioTrack.Builder().setAudioAttributes(
      AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .setUsage(AudioAttributes.USAGE_MEDIA).build()
    ).setBufferSizeInBytes(intSize).setAudioFormat(
      AudioFormat.Builder().setSampleRate(RECORDING_RATE).setChannelMask(CHANNELS_OUT)
        .setEncoding(FORMAT).build()
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


  companion object {
    const val RECORDING_RATE = 8000 // can go up to 44K, if needed
    const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
    const val CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO
    const val FORMAT = AudioFormat.ENCODING_PCM_16BIT
  }


}