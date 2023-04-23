package dev.tberghuis.voicememos

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MobileViewModel(private val application: Application) : AndroidViewModel(application) {
  val willitblend = "willitblend"


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