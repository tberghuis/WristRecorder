package dev.tberghuis.voicememos.tmp2

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.tberghuis.voicememos.service.RecordingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class TmpRecordingServiceManager(private val application: Application) {
  val recordingServiceFlow: MutableStateFlow<RecordingService?> = MutableStateFlow(null)

  private val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      val binder = service as RecordingService.LocalBinder
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
    val serviceIntent = Intent(application, RecordingService::class.java)
    application.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
  }

  suspend fun provideRecordingService(): RecordingService {
    return recordingServiceFlow.filterNotNull().first()
  }
}