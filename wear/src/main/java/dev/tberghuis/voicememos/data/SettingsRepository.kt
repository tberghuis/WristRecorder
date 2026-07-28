package dev.tberghuis.voicememos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository private constructor(
  private val dataStore: DataStore<Preferences>
) {
  private val BACK_OVERRIDE = booleanPreferencesKey("BACK_OVERRIDE")

  fun backOverrideFlow(): Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[BACK_OVERRIDE] ?: false
  }

  suspend fun updateBackOverride(value: Boolean) {
    dataStore.updateData {
      it.toMutablePreferences().also { preferences ->
        preferences[BACK_OVERRIDE] = value
      }
    }
  }

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
