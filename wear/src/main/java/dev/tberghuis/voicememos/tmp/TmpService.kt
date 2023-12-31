package dev.tberghuis.voicememos.tmp

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TmpService : LifecycleService() {

  var tmpJob: Job? = null
  var count = 0

  init {
    logd("TmpService init $this")
  }


  // todo val isForeground: MutableStateFlow = false
  //  lifecycleScope collect startForeground, stopForeground ...

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

    logd("TmpService onStartCommand $this")

    return START_NOT_STICKY
  }


  override fun onBind(intent: Intent): IBinder {
    super.onBind(intent)
    logd("TmpService onBind $this")
    return localBinder
  }

  override fun onUnbind(intent: Intent): Boolean {
    logd("TmpService onUnbind $this")
    return true
  }

  override fun onRebind(intent: Intent?) {
    super.onRebind(intent)
    logd("TmpService onRebind $this")
  }

  fun startTmpWork() {
//    var count = 0
    tmpJob = lifecycleScope.launch {
      while (true) {
        logd("doTmpWork $count")
        count++
        delay(1000)
      }
    }
  }


  fun stopTmpWork() {
    tmpJob?.cancel()
  }


}