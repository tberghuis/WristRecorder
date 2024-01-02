package dev.tberghuis.voicememos.tmp2

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.flow.MutableStateFlow

class TmpRecordingService : LifecycleService() {

  val isRecordingFlow = MutableStateFlow(false)

  private val localBinder = LocalBinder()

  inner class LocalBinder : Binder() {
    internal val recordingService: TmpRecordingService
      get() = this@TmpRecordingService
  }

  override fun onBind(intent: Intent): IBinder {
    super.onBind(intent)
    return localBinder
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_NOT_STICKY
  }




}