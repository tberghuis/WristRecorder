package dev.tberghuis.voicememos.tmp

import android.content.Intent
import android.os.Binder
import androidx.lifecycle.LifecycleService
import dev.tberghuis.voicememos.common.logd

class TmpService : LifecycleService() {

  private val localBinder = LocalBinder()

  inner class LocalBinder : Binder() {
    internal val tmpService: TmpService
      get() = this@TmpService
  }

  override fun onCreate() {
    super.onCreate()
    logd("TmpService onCreate")
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)

    logd("TmpService onStartCommand")

    return START_NOT_STICKY
  }


}