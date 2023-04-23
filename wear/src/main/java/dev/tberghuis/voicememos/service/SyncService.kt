package dev.tberghuis.voicememos.service

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.util.logd


class SyncService : WearableListenerService() {


  override fun onMessageReceived(messageEvent: MessageEvent) {
    super.onMessageReceived(messageEvent)
    logd("onMessageReceived messageEvent $messageEvent")
  }
}