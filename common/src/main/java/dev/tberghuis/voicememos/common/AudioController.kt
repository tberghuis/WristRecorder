package dev.tberghuis.voicememos.common

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
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


  // https://github.com/android/wear-os-samples/blob/main/WearSpeakerSample/wear/src/main/java/com/example/android/wearable/speaker/SoundRecorder.kt
  @RequiresPermission(Manifest.permission.RECORD_AUDIO)
  suspend fun record(filenameCallback: (String) -> Unit) {
    val intSize = AudioRecord.getMinBufferSize(RECORDING_RATE, CHANNEL_IN, FORMAT)

    val audioRecord =
      AudioRecord.Builder().setAudioSource(MediaRecorder.AudioSource.MIC).setAudioFormat(
        AudioFormat.Builder().setSampleRate(RECORDING_RATE).setChannelMask(CHANNEL_IN)
          .setEncoding(FORMAT).build()
      ).setBufferSizeInBytes(intSize * 3).build()

    val timestamp = System.currentTimeMillis()
    val recordingFileName = "wristrecorder_$timestamp.pcm"

    filenameCallback(recordingFileName)

    audioRecord.startRecording()

    try {
      withContext(Dispatchers.IO) {
        context.openFileOutput(recordingFileName, Context.MODE_PRIVATE).buffered()
          .use { bufferedOutputStream ->
            val buffer = ByteArray(intSize)
            while (isActive) {
              val read = audioRecord.read(buffer, 0, buffer.size)
              bufferedOutputStream.write(buffer, 0, read)
            }
          }
      }
    } finally {
      audioRecord.release()
      // failed attempt to rename file with duration
      //      val duration = ((System.currentTimeMillis() - timestamp) / 1000f).roundToInt()
      //      logd("duration $duration")
      //      delay(5000L)
      //      val recordingFile = File(context.filesDir, recordingFileName)
      //      recordingFile.renameTo(File(context.filesDir,"voicememo_${timestamp}_$duration.pcm"))
    }
  }


  companion object {
    const val RECORDING_RATE = 8000 // can go up to 44K, if needed
    const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
    const val CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO
    const val FORMAT = AudioFormat.ENCODING_PCM_16BIT
  }


}