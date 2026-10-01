package dev.tberghuis.voicememos

import android.app.Application
import androidx.work.Configuration

class MobileApplication : Application(), Configuration.Provider {
  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setMinimumLoggingLevel(android.util.Log.INFO)
      .build()
}
