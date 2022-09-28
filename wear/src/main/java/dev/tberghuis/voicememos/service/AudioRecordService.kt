package dev.tberghuis.voicememos.service

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import dev.tberghuis.voicememos.service.AudioConstants.Companion.CHANNEL_IN
import dev.tberghuis.voicememos.service.AudioConstants.Companion.RECORDING_RATE
import dev.tberghuis.voicememos.util.logd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt


// depends on app context
class AudioRecordService(val context: Context) {

  init {
    logd("AudioRecordService context $context")
  }


  @RequiresPermission(Manifest.permission.RECORD_AUDIO)
  suspend fun record(filenameCallback: (String) -> Unit) {
    val intSize = AudioRecord.getMinBufferSize(RECORDING_RATE, CHANNEL_IN, AudioConstants.FORMAT)

    val audioRecord =
      AudioRecord.Builder().setAudioSource(MediaRecorder.AudioSource.MIC).setAudioFormat(
        AudioFormat.Builder().setSampleRate(RECORDING_RATE).setChannelMask(CHANNEL_IN)
          .setEncoding(AudioConstants.FORMAT).build()
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


}