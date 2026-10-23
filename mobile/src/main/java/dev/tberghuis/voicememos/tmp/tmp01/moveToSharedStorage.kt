package dev.tberghuis.voicememos.tmp.tmp01

import android.app.Application
import android.content.Context
import dev.tberghuis.voicememos.common.deleteFileCommon
import kotlinx.coroutines.Dispatchers.IO
import android.content.ContentValues
import android.provider.MediaStore
import dev.tberghuis.voicememos.WR_RELATIVE_PATH
import dev.tberghuis.voicememos.common.AudioController.Companion.RECORDING_RATE
import dev.tberghuis.voicememos.tmp.tmp02.writeWaveHeader
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed interface CopyResult {
  data object Success : CopyResult
  data class Error(val message: String) : CopyResult
}

class MoveRecordingsVmc(
  private val application: Application,
  private val scope: CoroutineScope,
  private val refreshUi: () -> Unit,
  private val errorSnackbar: (String) -> Unit
) {
  fun moveToSharedStorage() {
    println("moveToSharedStorage")
    scope.launch(IO) {
      val files = getPcmFiles(application)
      files.forEach {
        val result = copyToSharedStorage(application, it)
        when (result) {
          is CopyResult.Error -> {
            // how to test?
            errorSnackbar(result.message)
          }

          CopyResult.Success -> {
            deleteFileCommon(application, it)
          }
        }
      }
      refreshUi()
    }
  }
}

private fun getPcmFiles(context: Context): List<String> {
  println("getPcmFiles")
  val pcmFiles = context.fileList().toList().filter { it.startsWith("wristrecorder_") }.sorted()
  println("pcmFiles $pcmFiles")
  return pcmFiles
}

suspend fun copyToSharedStorage(
  application: Application,
  pcmFileName: String
): CopyResult {
  println("moveToRecordings pcmFileName $pcmFileName")
  val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
  val pcmFile = File(application.filesDir, pcmFileName)
  val contentValues = ContentValues().apply {
    put(MediaStore.Audio.Media.DISPLAY_NAME, pcmFile.nameWithoutExtension)
    put(
      MediaStore.Audio.Media.MIME_TYPE,
      "audio/x-wav"
    )
    put(MediaStore.Audio.Media.RELATIVE_PATH, WR_RELATIVE_PATH)
    put(MediaStore.Audio.Media.IS_PENDING, 1)
  }
  val itemUri = application.contentResolver.insert(audioCollection, contentValues) ?: run {
    println("itemUri is null")
    return CopyResult.Error("itemUri is null")
  }
  application.contentResolver.openOutputStream(itemUri)?.use { outputStream ->

//    val longSampleRate = RECORDING_RATE.toLong()
//    val channels = 1
//    val byteRate = longSampleRate * channels * 16 / 8
    val totalAudioLen = pcmFile.length()
    writeWaveHeader(outputStream, totalAudioLen)

    FileInputStream(pcmFile).use { inputStream ->
      inputStream.copyTo(outputStream)
    }
  }
  contentValues.clear()
  contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
  application.contentResolver.update(itemUri, contentValues, null, null)

  return CopyResult.Success
}

fun xxxWriteWaveHeader(
  out: OutputStream,
  totalAudioLen: Long,
  longSampleRate: Long,
  channels: Int,
  byteRate: Long
) {
  val totalDataLen = totalAudioLen + 36
  val bitsPerSample = 16.toShort()
  val blockAlign = (channels * 16 / 8).toShort()

  val header = ByteArray(44)
  header[0] = 'R'.code.toByte() // RIFF/WAVE header
  header[1] = 'I'.code.toByte()
  header[2] = 'F'.code.toByte()
  header[3] = 'F'.code.toByte()
  header[4] = (totalDataLen and 0xff).toByte()
  header[5] = ((totalDataLen shr 8) and 0xff).toByte()
  header[6] = ((totalDataLen shr 16) and 0xff).toByte()
  header[7] = ((totalDataLen shr 24) and 0xff).toByte()
  header[8] = 'W'.code.toByte()
  header[9] = 'A'.code.toByte()
  header[10] = 'V'.code.toByte()
  header[11] = 'E'.code.toByte()
  header[12] = 'f'.code.toByte() // 'fmt ' chunk
  header[13] = 'm'.code.toByte()
  header[14] = 't'.code.toByte()
  header[15] = ' '.code.toByte()
  header[16] = 16 // 4 bytes: size of 'fmt ' chunk
  header[17] = 0
  header[18] = 0
  header[19] = 0
  header[20] = 1.toByte() // format = 1 (PCM)
  header[21] = 0
  header[22] = channels.toByte()
  header[23] = 0
  header[24] = (longSampleRate and 0xff).toByte()
  header[25] = ((longSampleRate shr 8) and 0xff).toByte()
  header[26] = ((longSampleRate shr 16) and 0xff).toByte()
  header[27] = ((longSampleRate shr 24) and 0xff).toByte()
  header[28] = (byteRate and 0xff).toByte()
  header[29] = ((byteRate shr 8) and 0xff).toByte()
  header[30] = ((byteRate shr 16) and 0xff).toByte()
  header[31] = ((byteRate shr 24) and 0xff).toByte()
  header[32] = blockAlign.toByte()
  header[33] = 0
  header[34] = bitsPerSample.toByte()
  header[35] = 0
  header[36] = 'd'.code.toByte() // 'data' chunk
  header[37] = 'a'.code.toByte()
  header[38] = 't'.code.toByte()
  header[39] = 'a'.code.toByte()
  header[40] = (totalAudioLen and 0xff).toByte()
  header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
  header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
  header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

  out.write(header, 0, 44)
}
