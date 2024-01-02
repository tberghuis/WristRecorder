package dev.tberghuis.voicememos.tmp

import android.app.Notification
import android.app.NotificationChannel
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
import dev.tberghuis.voicememos.R
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TmpService : LifecycleService() {

  // use lifecycleScope to run recording job
  var tmpJob: Job? = null
  var count = 0

  val isRecordingFlow = MutableStateFlow(false)


  // todo create notification channel in application

  init {
    logd("TmpService init $this")
  }

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

  fun startTmpWork() {
    logd("TmpService startTmpWork $this")

    isRecordingFlow.value = true

    val notification = generateNotification("main text?")
    startService(Intent(applicationContext, TmpService::class.java))
    startForeground(NOTIFICATION_ID, notification)


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
    isRecordingFlow.value = false
    stopForeground(STOP_FOREGROUND_REMOVE)
  }


  private fun generateNotification(mainText: String): Notification {
    logd("generateNotification")

    val titleText = "Walking Workout"
    val notificationChannel = NotificationChannel(
      NOTIFICATION_CHANNEL_ID,
      titleText,
      NotificationManager.IMPORTANCE_DEFAULT,
    )

    // todo createNotificationChannel in MainApplication
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    notificationManager.createNotificationChannel(notificationChannel)

    val bigTextStyle = NotificationCompat.BigTextStyle()
      .bigText(mainText)
      .setBigContentTitle(titleText)

    val launchActivityIntent = Intent(this, TmpActivity::class.java)

    val activityPendingIntent = PendingIntent.getActivity(
      this,
      0,
      launchActivityIntent,
      PendingIntent.FLAG_IMMUTABLE,
    )

    val notificationCompatBuilder =
      NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

    val notificationBuilder = notificationCompatBuilder
      .setStyle(bigTextStyle)
      .setContentTitle(titleText)
      .setContentText(mainText)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
      .setOngoing(true)
      .setCategory(NotificationCompat.CATEGORY_WORKOUT)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .addAction(
        R.drawable.ic_launcher_foreground,
        "Launch activity",
        activityPendingIntent,
      )

    val ongoingActivityStatus = Status.Builder()
      .addTemplate(mainText)
      .build()

    val ongoingActivity =
      OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
//        .setAnimatedIcon(R.drawable.animated_walk)
        .setStaticIcon(R.drawable.ic_launcher_foreground)
        .setTouchIntent(activityPendingIntent)
        .setStatus(ongoingActivityStatus)
        .build()

    ongoingActivity.apply(applicationContext)
    return notificationBuilder.build()
  }


  companion object {
    private const val NOTIFICATION_ID = 12345678
    private const val NOTIFICATION_CHANNEL_ID = "walking_workout_channel_01"
  }


}