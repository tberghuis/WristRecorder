package dev.tberghuis.voicememos

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.Wearable
import dev.tberghuis.voicememos.common.logd
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProcessZipWorker(private val context: Context, params: WorkerParameters) :
  CoroutineWorker(context, params) {
  private val nodeClient by lazy { Wearable.getNodeClient(context) }
  private val messageClient by lazy { Wearable.getMessageClient(context) }

  override suspend fun doWork(): Result {
    val recordingsZip = File("${context.filesDir.absolutePath}/recordings.zip")
    unzip(recordingsZip)
    recordingsZip.delete()
    return Result.success()
  }

  private suspend fun unzip(zipFile: File) {
    val zip = withContext(Dispatchers.IO) {
      ZipFile(zipFile)
    }
    val nodeId = nodeClient.localNode.await().id
    try {
      zip.entries().asSequence().map {
        val outputFile = File("${context.filesDir.absolutePath}/${it.name}")
        ZipIO(it, outputFile)
      }.forEach { (entry, output) ->
        zip.getInputStream(entry).use { input ->
          output.outputStream().use { output ->
            input.copyTo(output)
          }
        }
      }
      logd("unzip finished")
      messageClient.sendMessage(nodeId, "/sync-finished", byteArrayOf()).await()
    } catch (e: Exception) {
      logd("error $e")
      val ba = "error $e".toByteArray(Charsets.UTF_8)
      messageClient.sendMessage(nodeId, "/snackbar", ba).await()
    }
  }
}

data class ZipIO(val entry: ZipEntry, val output: File)
