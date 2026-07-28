package dev.tberghuis.voicememos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository private constructor(
  val dataStore: DataStore<Preferences>
) {
  // todo


  companion object {
    @Volatile
    private var INSTANCE: SettingsRepository? = null

    fun getInstance(context: Context): SettingsRepository {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: SettingsRepository(context.dataStore).also { INSTANCE = it }
      }
    }
  }


}


val Context.settingsRepository: SettingsRepository
  get() = SettingsRepository.getInstance(this)
