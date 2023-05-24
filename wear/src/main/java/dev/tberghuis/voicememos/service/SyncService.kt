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
  private val messageClient by lazy { Wearable.getMessageClient(application) }

  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")


    val phoneNodeId = messageEvent.data.toString(Charsets.UTF_8)
    when (messageEvent.path) {

      "/upload-recordings" -> {
//        val phoneNodeId = messageEvent.data.toString(Charsets.UTF_8)
        logd("/upload-recordings phoneNodeId $phoneNodeId")
        uploadRecordingsZip(phoneNodeId)
      }

      "/delete-all-watch" -> {
        logd("/delete-all-watch")
        deleteAllRecordings(phoneNodeId)
      }
    }
  }

  private fun uploadRecordingsZip(phoneNodeId: String) {
    val recordingsFileList = application.filesDir.listFiles()?.filter {
      it.isFile && it.name.startsWith("wristrecorder_")
    }

    if (recordingsFileList.isNullOrEmpty()) {
      // send message doitwrong
      // /snackbar or /snackbar-error

      scope.launch {
        val ba = "No recordings".toByteArray(Charsets.UTF_8)
        messageClient.sendMessage(phoneNodeId, "/snackbar", ba).await()
      }
      return
    }

    logd("uploadRecordingsZip recordingsFileList $recordingsFileList")

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

  private fun deleteAllRecordings(phoneNodeId: String) {
    val path = applicationContext.filesDir
    val files = path.listFiles() ?: return
    files.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        it.delete()
      }
    }

    scope.launch {
      val ba = "Delete complete".toByteArray(Charsets.UTF_8)
      messageClient.sendMessage(phoneNodeId, "/snackbar", ba).await()
    }


  }
}