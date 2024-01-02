package dev.tberghuis.voicememos.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.Wearable
import dev.tberghuis.voicememos.common.logd
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// todo move file to folder 'sync' (feature) instead of service

class UploadRecordingsWorker(private val context: Context, params: WorkerParameters) :
  CoroutineWorker(context, params) {
  private val channelClient by lazy { Wearable.getChannelClient(context) }
  private val messageClient by lazy { Wearable.getMessageClient(context) }

  override suspend fun doWork(): Result {
    val phoneNodeId = inputData.getString("PHONE_NODE_ID") ?: return Result.failure()
    val recordingsFileList = context.filesDir.listFiles()?.filter {
      it.isFile && it.name.startsWith("wristrecorder_")
    }
    if (recordingsFileList.isNullOrEmpty()) {
      // send message doitwrong
      // /snackbar or /snackbar-error
      withContext(IO) {
        val ba = "No recordings".toByteArray(Charsets.UTF_8)
        messageClient.sendMessage(phoneNodeId, "/snackbar", ba).await()
      }
      return Result.success()
    }
    logd("uploadRecordingsZip recordingsFileList $recordingsFileList")
    val channelTask = channelClient.openChannel(phoneNodeId, "/sendzip")
    withContext(IO) {
      val channel = channelTask.await()
      val outputStream = channelClient.getOutputStream(channel).await()
      ZipOutputStream(BufferedOutputStream(outputStream)).use { zos ->
        recordingsFileList.forEach { recordingFile ->
          FileInputStream(recordingFile).use { fis ->
            BufferedInputStream(fis).use {
              val entry = ZipEntry(recordingFile.name)
              zos.putNextEntry(entry)
              fis.copyTo(zos, 1024)
            }
          }
        }
      }
      logd("uploadRecordingsZip sent")
    }
    return Result.success()
  }
}