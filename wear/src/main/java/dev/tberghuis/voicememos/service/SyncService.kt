package dev.tberghuis.voicememos.service

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.ExecutionException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncService : WearableListenerService() {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val channelClient by lazy { Wearable.getChannelClient(applicationContext) }

  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")
    when (messageEvent.path) {
      "/msg-sync-recordings" -> {
        logd("/msg-sync-recordings")
        syncRecordings()
      }

      "/upload-recordings" -> {
        val phoneNodeId = messageEvent.data.toString(Charsets.UTF_8)
        logd("/upload-recordings phoneNodeId $phoneNodeId")
        uploadRecordings(phoneNodeId)
      }

      "/delete-all-watch" -> {
        logd("/delete-all-watch")
        deleteAllRecordings()
      }
    }
  }

  private fun uploadRecordings(phoneNodeId: String) {
    val filesDirListFiles = filesDir.listFiles() ?: return
    val recordings = mutableListOf<File>()
    // wristrecorder_1682309581196.pcm
    filesDirListFiles.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        recordings.add(it)
      }
    }
    if (recordings.isEmpty()) {
      return
    }

    // todo better channel path name
    val channelTask = channelClient.openChannel(phoneNodeId, "/sendzip")
    scope.launch {
      val channel = channelTask.await()
      val outputStreamTask = channelClient.getOutputStream(channel)
      val os = outputStreamTask.await()
      val bos = BufferedOutputStream(os)
      val zos = ZipOutputStream(bos)

      recordings.forEach { recordingFile ->
        BufferedInputStream(FileInputStream(recordingFile)).use { bis ->
          zos.putNextEntry(ZipEntry(recordingFile.name))
          bis.copyTo(zos, 1024)
        }
//        zos.closeEntry()
      }
//      zos.flush()
      zos.close()
//      bos.flush()
//      bos.close()
//      os.flush()
//      os.close()
      channelClient.close(channel)
      logd("zip sent")
    }
  }

  @SuppressLint("VisibleForTests")
  private fun syncRecordings() {
    logd("syncRecordings")
    val path = applicationContext.filesDir
    val files = path.listFiles() ?: return
    val recordings = mutableListOf<File>()
    // wristrecorder_1682309581196.pcm
    files.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        recordings.add(it)
      }
    }
    logd("recordings $recordings")
    val assets = recordings.map {
      Asset.createFromBytes(it.readBytes())
    }
    val request = PutDataMapRequest.create("/sync-recordings").apply {
      recordings.forEachIndexed { i, file ->
        dataMap.putAsset(file.name, assets[i])
      }
    }.asPutDataRequest().setUrgent()

    val putTask: Task<DataItem> = Wearable.getDataClient(applicationContext).putDataItem(request)
    logd("putTask $putTask")

    try {
      Tasks.await(putTask).apply {
        logd("data item set")
      }
      deleteAllRecordings()
    } catch (e: ExecutionException) {
      logd("ExecutionException $e")
    } catch (e: InterruptedException) {
      logd("InterruptedException $e")
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