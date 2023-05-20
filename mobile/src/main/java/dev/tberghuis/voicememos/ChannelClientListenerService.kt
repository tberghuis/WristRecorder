package dev.tberghuis.voicememos

import android.net.Uri
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChannelClientListenerService : WearableListenerService() {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val channelClient by lazy { Wearable.getChannelClient(applicationContext) }

  override fun onDestroy() {
    super.onDestroy()
    scope.cancel()
  }


  override fun onChannelOpened(channel: ChannelClient.Channel) {
    super.onChannelOpened(channel)

    logd("onChannelOpened")

    // doitwrong
    val zipfile = File("${application.filesDir.absolutePath}/test.zip")
    val zipfileuri = Uri.fromFile(zipfile)


    val task = channelClient.receiveFile(channel, zipfileuri, false)

    channelClient.registerChannelCallback(channel, object : ChannelClient.ChannelCallback() {
      override fun onInputClosed(
        channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int
      ) {
        super.onInputClosed(channel, closeReason, appSpecificErrorCode)
        logd("onInputClosed closeReason $closeReason appSpecificErrorCode $appSpecificErrorCode")

        // todo here is where to update user pref refreshUiToggle so app reads list of files

        channelClient.unregisterChannelCallback(this)
        // todo call unzip
        // todo delete zip
        // todo toggle user pref refreshUi

      }
    })

    scope.launch {
      task.await()
      logd("after task await")
    }
  }
}