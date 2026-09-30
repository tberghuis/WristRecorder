package dev.tberghuis.voicememos

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.tberghuis.voicememos.common.logd
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProcessZipWorker(private val context: Context, params: WorkerParameters) :
  CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    logd("ProcessZipWorker doWork")
    val recordingsZip = File("${context.filesDir.absolutePath}/recordings.zip")
    unzip(recordingsZip)
    recordingsZip.delete()
    logd("ProcessZipWorker doWork finished")
    return Result.success()
  }

  private suspend fun unzip(zipFile: File) {
    val zip = withContext(Dispatchers.IO) {
      ZipFile(zipFile)
    }
    val intent = Intent("ProcessZipResult").apply {
      setPackage(context.packageName)
    }

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
      intent.putExtra("result", "success")
    } catch (e: Exception) {
      logd("error $e")
      intent.putExtra("result", "error")
      intent.putExtra("message", "$e")
    } finally {
      context.sendBroadcast(intent)
    }
  }
}

data class ZipIO(val entry: ZipEntry, val output: File)