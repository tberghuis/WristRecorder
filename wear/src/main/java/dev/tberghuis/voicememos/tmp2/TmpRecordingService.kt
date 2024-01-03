package dev.tberghuis.voicememos.tmp2

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import dev.tberghuis.voicememos.common.AudioController
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

class TmpRecordingService : LifecycleService() {
  private lateinit var audioController: AudioController
  var recordingJob: Job? = null
  val isRecordingFlow = MutableStateFlow(false)
  var filename: String? = null

  private val localBinder = LocalBinder()

  inner class LocalBinder : Binder() {
    internal val recordingService: TmpRecordingService
      get() = this@TmpRecordingService
  }

  override fun onBind(intent: Intent): IBinder {
    super.onBind(intent)
    return localBinder
  }

  override fun onCreate() {
    super.onCreate()
    audioController = AudioController(application)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_NOT_STICKY
  }

  // doitwrong
  fun startRecording() {
    logd("startRecording")
    isRecordingFlow.value = true
    // todo set filename in callback
  }

  fun stopRecording(): String? {
    logd("stopRecording")
    isRecordingFlow.value = false
    return filename
  }

}