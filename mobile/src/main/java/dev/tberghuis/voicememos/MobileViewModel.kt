package dev.tberghuis.voicememos

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
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
  private val audioController = AudioController(application)
  val recordingFilesStateFlow = MutableStateFlow(listOf<File>())

  var confirmDeleteDialog by mutableStateOf(false)

  val snackbarHostState = SnackbarHostState()
  private val messageClient = Wearable.getMessageClient(application)
  private val nodeClient = Wearable.getNodeClient(application)

  private val messageListener = MessageClient.OnMessageReceivedListener { messageEvent ->
    when (messageEvent.path) {
      "/snackbar" -> {
        viewModelScope.launch {
          snackbarHostState.showSnackbar(messageEvent.data.toString(Charsets.UTF_8))
        }
      }

      "/sync-finished" -> {
        viewModelScope.launch {
          refreshRecordingFiles()
          snackbarHostState.showSnackbar("Download complete")
        }
      }
    }
  }

  init {
    logd("MobileViewModel init")
    refreshRecordingFiles()
    messageClient.addListener(messageListener)
  }

  override fun onCleared() {
    messageClient.removeListener(messageListener)
    super.onCleared()
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

    viewModelScope.launch {
      try {
        val nodeId = nodeClient.localNode.await().id
        sendMessageWatch("/upload-recordings", nodeId.toByteArray(Charsets.UTF_8))
      } catch (e: Exception) {
        logd("error $e")
      }
    }
  }

  private fun sendMessageWatch(messagePath: String, byteArray: ByteArray) {
    val capabilityClient = Wearable.getCapabilityClient(application)


    viewModelScope.launch {
      try {
        val nodes =
          capabilityClient.getCapability("wear", CapabilityClient.FILTER_REACHABLE).await().nodes
        // no watches found
        if (nodes.isEmpty()) {
          snackbarHostState.showSnackbar("No watch connected")
          return@launch
        }
        nodes.map { node ->
          async {
            messageClient.sendMessage(node.id, messagePath, byteArray).await()
          }
        }.awaitAll()
        logd("message sent success")

      } catch (e: Exception) {
        logd("error: $e")
        snackbarHostState.showSnackbar("error: $e")
      }
    }
  }

  fun deleteAllWatch() {
    viewModelScope.launch {
      val nodeId = nodeClient.localNode.await().id
      sendMessageWatch("/delete-all-watch", nodeId.toByteArray(Charsets.UTF_8))
    }
  }
}