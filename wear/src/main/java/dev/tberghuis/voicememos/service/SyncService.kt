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
import dev.tberghuis.voicememos.util.logd
import java.io.File
import java.util.concurrent.ExecutionException


class SyncService : WearableListenerService() {


  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")


    when (messageEvent.path) {
      "/msg-sync-recordings" -> {
        logd("/msg-sync-recordings")
        syncRecordings()
      }
    }

  }


  @SuppressLint("VisibleForTests")
  private fun syncRecordings() {
    logd("syncRecordings")
    val path = applicationContext.filesDir

    val files = path.listFiles()
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
    val files = path.listFiles()
    files.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        it.delete()
      }
    }
  }


}