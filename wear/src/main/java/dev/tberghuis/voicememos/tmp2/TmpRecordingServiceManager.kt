package dev.tberghuis.voicememos.tmp2

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class TmpRecordingServiceManager(private val application: Application) {
  val recordingServiceFlow: MutableStateFlow<TmpRecordingService?> = MutableStateFlow(null)

  private val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      val binder = service as TmpRecordingService.LocalBinder
      recordingServiceFlow.value = binder.recordingService
    }
    override fun onServiceDisconnected(name: ComponentName) {
      recordingServiceFlow.value = null
    }
  }

  init {
    setupBind()
  }

  private fun setupBind() {
    val serviceIntent = Intent(application, TmpRecordingService::class.java)
    application.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
  }

  suspend fun provideRecordingService(): TmpRecordingService {
    return recordingServiceFlow.filterNotNull().first()
  }
}