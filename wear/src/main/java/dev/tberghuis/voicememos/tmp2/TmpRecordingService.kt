package dev.tberghuis.voicememos.tmp2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.tberghuis.voicememos.common.AudioController
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TmpRecordingService : LifecycleService() {
  val isRecordingFlow = MutableStateFlow(false)

  private lateinit var audioController: AudioController
  private var recordingJob: Job? = null
  private var filename: String? = null

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
  @SuppressLint("MissingPermission")
  fun startRecording() {
    logd("startRecording")
    isRecordingFlow.value = true
    // do i need to change dispatcher?
    recordingJob = lifecycleScope.launch {
      audioController.record { filename = it }
    }
  }

  fun stopRecording(): String? {
    logd("stopRecording")
    isRecordingFlow.value = false
    recordingJob?.cancel()
    return filename
  }
}