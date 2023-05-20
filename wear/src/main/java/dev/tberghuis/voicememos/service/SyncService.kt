package dev.tberghuis.voicememos.service

import android.net.Uri
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SyncService : WearableListenerService() {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val channelClient by lazy { Wearable.getChannelClient(applicationContext) }

  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")
    when (messageEvent.path) {
      "/upload-recordings" -> {
        val phoneNodeId = messageEvent.data.toString(Charsets.UTF_8)
        logd("/upload-recordings phoneNodeId $phoneNodeId")
        uploadRecordingsZip(phoneNodeId)
      }
      "/delete-all-watch" -> {
        logd("/delete-all-watch")
        deleteAllRecordings()
      }
    }
  }

  private fun uploadRecordingsZip(phoneNodeId: String) {
    val recordingsFileList = application.filesDir.listFiles()?.filter {
      it.isFile && it.name.startsWith("wristrecorder_")
    } ?: return

    val channelTask = channelClient.openChannel(phoneNodeId, "/sendzip")

    scope.launch {
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
  }

  private fun deleteAllRecordings() {
    val path = applicationContext.filesDir
    val files = path.listFiles() ?: return
    files.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        it.delete()
      }
    }
  }
}