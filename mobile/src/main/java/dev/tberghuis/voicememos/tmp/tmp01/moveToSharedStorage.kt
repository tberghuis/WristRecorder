package dev.tberghuis.voicememos.tmp.tmp01

import android.app.Application
import android.content.Context
import dev.tberghuis.voicememos.common.deleteFileCommon
import kotlinx.coroutines.Dispatchers.IO
import android.content.ContentValues
import android.provider.MediaStore
import dev.tberghuis.voicememos.WR_RELATIVE_PATH
import dev.tberghuis.voicememos.common.createWavHeader
import java.io.File
import java.io.FileInputStream
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
    val totalAudioLen = pcmFile.length()
    val header = createWavHeader(totalAudioLen)
    outputStream.write(header, 0, 44)

    FileInputStream(pcmFile).use { inputStream ->
      inputStream.copyTo(outputStream)
    }
  }
  contentValues.clear()
  contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
  application.contentResolver.update(itemUri, contentValues, null, null)

  return CopyResult.Success
}