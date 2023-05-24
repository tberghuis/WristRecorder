package dev.tberghuis.voicememos

import android.net.Uri
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.File
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChannelClientListenerService : WearableListenerService() {
  // wearos samples uses Dispatchers.Main.immediate ???
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val channelClient by lazy { Wearable.getChannelClient(applicationContext) }
  private val messageClient by lazy { Wearable.getMessageClient(application) }
  private val nodeClient by lazy { Wearable.getNodeClient(application) }

  override fun onDestroy() {
    super.onDestroy()
    scope.cancel()
  }

  override fun onChannelOpened(channel: ChannelClient.Channel) {
    super.onChannelOpened(channel)
    logd("onChannelOpened")

    // doitwrong
    // this won't work if using multiple watches
    // should name recordings.zip to <watch node id>_recordings.zip
    val zipfile = File("${application.filesDir.absolutePath}/recordings.zip")
    val zipfileuri = Uri.fromFile(zipfile)

    val task = channelClient.receiveFile(channel, zipfileuri, false)

    channelClient.registerChannelCallback(channel, object : ChannelClient.ChannelCallback() {
      override fun onInputClosed(
        channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int
      ) {
        super.onInputClosed(channel, closeReason, appSpecificErrorCode)
        logd("onInputClosed closeReason $closeReason appSpecificErrorCode $appSpecificErrorCode")

        channelClient.unregisterChannelCallback(channel, this)

        // could the problem be that i was trying to close channel from the sending side
        // but closing too early???

        channelClient.close(channel)
        processZip()
      }
    })

    scope.launch {
      task.await()
//      channelClient.close(channel)
//      processZip()
      logd("after task await")
    }
  }

  private fun processZip() {
    val recordingsZip = File("${application.filesDir.absolutePath}/recordings.zip")
    scope.launch {
      unzip(recordingsZip)
      recordingsZip.delete()
    }
  }

  private suspend fun unzip(zipFile: File) {
    val zip = ZipFile(zipFile)
    val nodeId = nodeClient.localNode.await().id

    try {
      zip.entries().asSequence().map {
        val outputFile = File("${application.filesDir.absolutePath}/${it.name}")
        ZipIO(it, outputFile)
      }.forEach { (entry, output) ->
        zip.getInputStream(entry).use { input ->
          output.outputStream().use { output ->
            input.copyTo(output)
          }
        }
      }
      logd("unzip finished")
      messageClient.sendMessage(nodeId, "/sync-finished", byteArrayOf()).await()
    } catch (e: Exception) {
      logd("error $e")
      val ba = "error $e".toByteArray(Charsets.UTF_8)
      messageClient.sendMessage(nodeId, "/snackbar", ba).await()
    }
  }
}

data class ZipIO(val entry: ZipEntry, val output: File)
