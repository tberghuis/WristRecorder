package dev.tberghuis.voicememos.tmp

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd

class MessageServiceTmp : WearableListenerService() {


  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")
  }
}