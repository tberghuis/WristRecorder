package dev.tberghuis.voicememos

import android.app.Application
import android.net.Uri
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.EOFException
import java.io.File
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChannelClientListenerService : WearableListenerService() {
  // wearos samples uses Dispatchers.Main.immediate ???
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val channelClient by lazy { Wearable.getChannelClient(applicationContext) }

  private val dataStoreRepository by lazy { DataStoreRepository(applicationContext.dataStore) }

  override fun onDestroy() {
    super.onDestroy()
    scope.cancel()
  }


  override fun onChannelOpened(channel: ChannelClient.Channel) {
    super.onChannelOpened(channel)
    logd("onChannelOpened")

    // doitwrong
    val zipfile = File("${application.filesDir.absolutePath}/recordings.zip")
    val zipfileuri = Uri.fromFile(zipfile)
    val task = channelClient.receiveFile(channel, zipfileuri, false)

    channelClient.registerChannelCallback(channel, object : ChannelClient.ChannelCallback() {
      override fun onInputClosed(
        channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int
      ) {
        super.onInputClosed(channel, closeReason, appSpecificErrorCode)
        logd("onInputClosed closeReason $closeReason appSpecificErrorCode $appSpecificErrorCode")
//        channelClient.unregisterChannelCallback(this)
//        channelClient.close(channel)
        processZip()
      }
    })

    scope.launch {
      task.await()
      logd("after task await")
    }
  }

  private fun processZip() {
    val recordingsZip = File("${application.filesDir.absolutePath}/recordings.zip")
    scope.launch {
      delay(500L)
      unzip(recordingsZip, application)
      dataStoreRepository.syncRecordingsComplete()
//      recordingsZip.delete()
    }
  }
}

data class ZipIO(val entry: ZipEntry, val output: File)

fun unzip(zipFile: File, application: Application) {
  val zip = ZipFile(zipFile)

  try {
    zip.entries().asSequence().map {
      val outputFile = File("${application.filesDir.absolutePath}/${it.name}")
      ZipIO(it, outputFile)
    }.forEach { (entry, output) ->
      zip.getInputStream(entry).use { input ->
        output.outputStream().use { output ->
          try {
            input.copyTo(output)
          } catch (e: EOFException) {
            logd("EOFException $e")
          }
        }
      }
    }
    logd("unzip finished")
  } catch (e: Exception) {
    logd("error $e")
  }


}

