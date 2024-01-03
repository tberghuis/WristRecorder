package dev.tberghuis.voicememos.tmp2

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import dev.tberghuis.voicememos.Constants.NOTIFICATION_CHANNEL_ID
import dev.tberghuis.voicememos.Constants.NOTIFICATION_ID
import dev.tberghuis.voicememos.Constants.REQUEST_CODE_LAUNCH_MAIN_ACTIVITY
import dev.tberghuis.voicememos.MainActivity
import dev.tberghuis.voicememos.R
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

    val notification = generateNotification()
    startService(Intent(applicationContext, TmpRecordingService::class.java))
    startForeground(NOTIFICATION_ID, notification)

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

  private fun generateNotification(): Notification {
    logd("generateNotification")

    val launchActivityIntent = Intent(this, MainActivity::class.java)

    val activityPendingIntent = PendingIntent.getActivity(
      this,
      REQUEST_CODE_LAUNCH_MAIN_ACTIVITY,
      launchActivityIntent,
      PendingIntent.FLAG_IMMUTABLE,
    )

    val notificationBuilder =
      NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(getString(R.string.recording))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setOngoing(true)
        .addAction(
          R.drawable.ic_recording,
          getString(R.string.open_wrist_recorder),
          activityPendingIntent,
        )

    val ongoingActivityStatus = Status.Builder()
      .addTemplate(getString(R.string.recording))
      .build()

    val ongoingActivity =
      OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
        .setStaticIcon(R.drawable.ic_recording)
        .setTouchIntent(activityPendingIntent)
        .setStatus(ongoingActivityStatus)
        .build()

    ongoingActivity.apply(applicationContext)
    return notificationBuilder.build()
  }
}