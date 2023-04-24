package dev.tberghuis.voicememos

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MobileViewModel(private val application: Application) : AndroidViewModel(application) {
  private val dataStoreRepository = DataStoreRepository(application.applicationContext.dataStore)

  val recordingFilesStateFlow = MutableStateFlow(listOf<File>())

  init {
    logd("MobileViewModel init")

    viewModelScope.launch {
      dataStoreRepository.syncRecordingsCompleteFlow.collect {
        logd("syncRecordingsCompleteFlow $it")
        refreshRecordingFiles()
      }
    }
  }

  private fun refreshRecordingFiles() {
    val path = application.filesDir
    val files = path.listFiles() ?: return
    val recordingFiles = mutableListOf<File>()
    files.forEach {
      if (it.isFile && it.name.startsWith("wristrecorder_")) {
        recordingFiles.add(it)
      }
    }
    recordingFilesStateFlow.value = recordingFiles
  }

  fun syncRecordings(activity: Activity) {
    val capabilityClient = Wearable.getCapabilityClient(activity)
    val messageClient = Wearable.getMessageClient(activity)
    viewModelScope.launch {
      val nodes = capabilityClient
        .getCapability("wear", CapabilityClient.FILTER_REACHABLE)
        .await()
        .nodes
      logd("nodes: $nodes")
      nodes.map { node ->
        async {
          messageClient.sendMessage(node.id, "/msg-sync-recordings", byteArrayOf())
            .await()
        }
      }.awaitAll()
      logd("message sent success")
    }

  }
}