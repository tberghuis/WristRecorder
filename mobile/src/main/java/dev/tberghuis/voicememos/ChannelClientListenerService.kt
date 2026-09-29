package dev.tberghuis.voicememos

import android.content.Intent
import android.net.Uri
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import kotlinx.coroutines.GlobalScope
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
//        super.onInputClosed(channel, closeReason, appSpecificErrorCode)
        logd("onInputClosed closeReason $closeReason appSpecificErrorCode $appSpecificErrorCode")

//        channelClient.unregisterChannelCallback(channel, this)

        // could the problem be that i was trying to close channel from the sending side
        // but closing too early???

//        channelClient.close(channel)
        processZipWorker()
      }
    })

//    scope.launch {
//      task.await()
//      logd("after task await")
//    }
  }

  private fun processZipWorker() {
    val worker = OneTimeWorkRequestBuilder<ProcessZipWorker>()
      .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
      .build()

    GlobalScope.launch {
      WorkManager.getInstance(application).getWorkInfoByIdFlow(worker.id).collect { workInfo ->
        println("workInfo $workInfo")
        println("isFinished ${workInfo?.state?.isFinished}")

        if (workInfo != null && workInfo.state.isFinished) {

          val intent = Intent("ProcessZipResult")

          when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
              intent.putExtra("result", "success")
            }

            WorkInfo.State.FAILED -> {
              intent.putExtra("result", "error")
              intent.putExtra("message", "todo error message")
            }

            else -> {}
          }
          sendBroadcast(intent,RECEIVER_NOT_EXPORTED)
        }
      }
    }





    WorkManager.getInstance(application).enqueueUniqueWork(
      "PROCESS_ZIP",
      ExistingWorkPolicy.KEEP,
      worker
    )
  }
}