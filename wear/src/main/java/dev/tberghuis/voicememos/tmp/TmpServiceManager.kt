package dev.tberghuis.voicememos.tmp

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class TmpServiceManager(private val application: Application) {
  private var tmpService: MutableStateFlow<TmpService?> = MutableStateFlow(null)

  private val tmpServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      logd("onServiceConnected")
      val binder = service as TmpService.LocalBinder
      tmpService.value = binder.tmpService
    }

    override fun onServiceDisconnected(name: ComponentName) {
      logd("onServiceDisconnected")
      tmpService.value = null
    }
  }

  init {
    setupBind()
  }

  private fun setupBind() {
    val serviceIntent = Intent(application, TmpService::class.java)
    // does using BIND_AUTO_CREATE matter?
    // run some tests
    application.bindService(serviceIntent, tmpServiceConnection, Context.BIND_AUTO_CREATE)
  }

  suspend fun provideTmpService(): TmpService {
    return tmpService.filterNotNull().first()
  }


}