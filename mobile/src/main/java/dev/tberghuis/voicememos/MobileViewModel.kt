package dev.tberghuis.voicememos

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import dev.tberghuis.voicememos.common.AudioController
import dev.tberghuis.voicememos.common.deleteFileCommon
import dev.tberghuis.voicememos.common.logd
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MobileViewModel(private val application: Application) : AndroidViewModel(application) {
  private val dataStoreRepository = DataStoreRepository(application.applicationContext.dataStore)
  private val audioController = AudioController(application)
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
      try {
        val nodes =
          capabilityClient.getCapability("wear", CapabilityClient.FILTER_REACHABLE).await().nodes
        logd("nodes: $nodes")
        nodes.map { node ->
          async {
            messageClient.sendMessage(node.id, "/msg-sync-recordings", byteArrayOf()).await()
          }
        }.awaitAll()
        logd("message sent success")

      } catch (e: Exception) {
        logd("error: $e")
      }
    }
  }

  fun playRecording(file: File) {
    viewModelScope.launch {
      audioController.play(file.name)
    }
  }

  fun deleteRecording(file: File) {
    deleteFileCommon(application, file.name)
    refreshRecordingFiles()
  }

  fun downloadRecordings() {
    val nodeClient = Wearable.getNodeClient(application)
    val nodeTask = nodeClient.localNode
    viewModelScope.launch {
      val nodeId = nodeTask.await().id
      sendMessageWatch("/upload-recordings", nodeId.toByteArray(Charsets.UTF_8))
    }
  }

  private fun sendMessageWatch(messagePath: String, byteArray: ByteArray) {
    val capabilityClient = Wearable.getCapabilityClient(application)
    val messageClient = Wearable.getMessageClient(application)

    viewModelScope.launch {
      try {
        val nodes =
          capabilityClient.getCapability("wear", CapabilityClient.FILTER_REACHABLE).await().nodes
        logd("nodes: $nodes")
        nodes.map { node ->
          async {
            messageClient.sendMessage(node.id, messagePath, byteArray).await()
          }
        }.awaitAll()
        logd("message sent success")

      } catch (e: Exception) {
        logd("error: $e")
      }
    }

  }

  fun deleteAllWatch() {
    // todo
  }


}