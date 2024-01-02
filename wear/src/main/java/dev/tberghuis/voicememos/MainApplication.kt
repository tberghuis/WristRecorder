package dev.tberghuis.voicememos

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dev.tberghuis.voicememos.Constants.NOTIFICATION_CHANNEL_ID

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    val notificationChannel = NotificationChannel(
      NOTIFICATION_CHANNEL_ID,
      // todo use stringResource
      "Recording",
      NotificationManager.IMPORTANCE_DEFAULT,
    )
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
  }

}