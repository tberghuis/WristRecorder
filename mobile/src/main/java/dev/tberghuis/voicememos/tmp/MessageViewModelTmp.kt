package dev.tberghuis.voicememos.tmp

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MessageViewModelTmp(private val application: Application) : AndroidViewModel(application) {

  val messageClient = Wearable.getMessageClient(application)
  val nodeClient = Wearable.getNodeClient(application)


  //  todo showSnackbarSharedFlow: String
  val snackbarHostState = SnackbarHostState()


  val messageListener = MessageClient.OnMessageReceivedListener { messageEvent ->
    logd("initMessageListener $messageEvent")

    when (messageEvent.path) {
      "/willitblend-activity" -> {
        logd("todo snackbar message")
        viewModelScope.launch {
          snackbarHostState.showSnackbar("todo snackbar message")
        }
      }
    }

  }

  init {
    initMessageListener()
  }


  fun willitblend() {
    logd("willitblend")


    viewModelScope.launch {
      val nodeId = nodeClient.localNode.await().id

      val result = messageClient.sendMessage(nodeId, "/willitblend-service", byteArrayOf()).await()

      logd("willitblend nodeId $nodeId result $result")
    }

  }


  fun initMessageListener() {
    messageClient.addListener(messageListener)
  }

  override fun onCleared() {
    // todo messageClient.removeListener
    messageClient.removeListener(messageListener)
    super.onCleared()
  }
}