package dev.tberghuis.voicememos.tmp

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessageServiceTmp : WearableListenerService() {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  val messageClient by lazy { Wearable.getMessageClient(application) }
  val nodeClient by lazy { Wearable.getNodeClient(application) }

  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")


    when (messageEvent.path) {
      "/willitblend-service" -> {
        sendMessageToActivity()
      }
    }
  }


  private fun sendMessageToActivity() {
    scope.launch {
      val nodeId = nodeClient.localNode.await().id

      val result = messageClient.sendMessage(nodeId, "/willitblend-activity", byteArrayOf()).await()

      logd("sendMessageToActivity nodeId $nodeId result $result")
    }
  }
}