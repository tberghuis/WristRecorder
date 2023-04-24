package dev.tberghuis.voicememos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(
  name = "user_preferences",
)

class DataStoreRepository(private val dataStore: DataStore<Preferences>) {

  // doitwrong
  // refresh on int value changed
  val syncRecordingsCompleteFlow: Flow<Int> = dataStore.data.map { preferences ->
    preferences[intPreferencesKey("sync_complete")] ?: 0
  }

  suspend fun syncRecordingsComplete() {
    dataStore.edit { preferences ->
      val syncComplete = preferences[intPreferencesKey("sync_complete")] ?: 0
      preferences[intPreferencesKey("sync_complete")] = (syncComplete + 1) % 2
    }
  }
}
