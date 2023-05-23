package dev.tberghuis.voicememos.tmp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Wearable
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MessageViewModelTmp(private val application: Application) : AndroidViewModel(application) {

  val messageClient = Wearable.getMessageClient(application)
  val nodeClient = Wearable.getNodeClient(application)


//  todo showSnackbarSharedFlow: String

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
    messageClient.addListener { messageEvent ->
      logd("initMessageListener $messageEvent")

      when (messageEvent.path) {
        "/willitblend-activity" -> {
          logd("todo emit to snackbar shared flow")
        }
      }

    }
  }

  override fun onCleared() {
    // todo messageClient.removeListener

    super.onCleared()
  }
}