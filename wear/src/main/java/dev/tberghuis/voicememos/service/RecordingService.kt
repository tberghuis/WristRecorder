package dev.tberghuis.voicememos.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
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
import dev.tberghuis.voicememos.formatSecondsToMinutesAndSeconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordingService : LifecycleService() {

  private lateinit var notificationManager: NotificationManager


  val isRecordingFlow = MutableStateFlow(false)

  private lateinit var audioController: AudioController
  private var recordingJob: Job? = null
  private var filename: String? = null

  private val localBinder = LocalBinder()

  inner class LocalBinder : Binder() {
    internal val recordingService: RecordingService
      get() = this@RecordingService
  }

  override fun onBind(intent: Intent): IBinder {
    super.onBind(intent)
    return localBinder
  }

  override fun onCreate() {
    super.onCreate()
    notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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

    val notification = generateNotification(0)
    startService(Intent(applicationContext, RecordingService::class.java))
    startForeground(NOTIFICATION_ID, notification)

    recordingJob = lifecycleScope.launch {
      logd("recordingJob launch")
      launch {
        audioController.record { filename = it }
      }
      for (i in generateSequence(1) { it + 1 }) {
        delay(1000)
        val updateNotification = generateNotification(i)
        logd("generateSequence $i")
        notificationManager.notify(NOTIFICATION_ID, updateNotification)
      }
    }
  }

  fun stopRecording(): String? {
    logd("stopRecording")
    recordingJob?.cancel()
    stopForeground(STOP_FOREGROUND_REMOVE)
    isRecordingFlow.value = false
    return filename
  }

  private fun generateNotification(recordingDurationSeconds: Int): Notification {
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
      .addTemplate("REC ${formatSecondsToMinutesAndSeconds(recordingDurationSeconds)}")
      .build()

    val ongoingActivity =
      OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
        .setStaticIcon(R.drawable.ic_recording)
        // todo
        .setAnimatedIcon(R.drawable.animated_walk)
        .setTouchIntent(activityPendingIntent)
        .setStatus(ongoingActivityStatus)
        .build()

    ongoingActivity.apply(applicationContext)
    return notificationBuilder.build()
  }
}