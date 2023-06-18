package dev.tberghuis.voicememos.service

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
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
        logd("/upload-recordings phoneNodeId $phoneNodeId")
        uploadRecordingsZipWorker(phoneNodeId)
      }

      "/delete-all-watch" -> {
        logd("/delete-all-watch")
        deleteAllRecordings(phoneNodeId)
      }
    }
  }

  private fun uploadRecordingsZipWorker(phoneNodeId: String) {
    val data = Data.Builder()
      .putString("PHONE_NODE_ID", phoneNodeId)
      .build()
    val worker = OneTimeWorkRequestBuilder<UploadRecordingsWorker>()
      .setInputData(data)
      .build()

    WorkManager.getInstance(application).enqueueUniqueWork(
      "UPLOAD_RECORDINGS",
      ExistingWorkPolicy.KEEP,
      worker
    )
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