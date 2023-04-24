package dev.tberghuis.voicememos

import android.annotation.SuppressLint
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import dev.tberghuis.voicememos.common.logd
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataClientListenerService : WearableListenerService() {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  lateinit var dataStoreRepository: DataStoreRepository

  override fun onCreate() {
    super.onCreate()
    dataStoreRepository = DataStoreRepository(applicationContext.dataStore)
  }

  @SuppressLint("VisibleForTests")
  override fun onDataChanged(dataEvents: DataEventBuffer) {
    super.onDataChanged(dataEvents)
    logd("DataClientListenerService onDataChanged dataEvents: $dataEvents")

    dataEvents.forEach { dataEvent ->
      when (dataEvent.dataItem.uri.path) {
        "/sync-recordings" -> {
          val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
          val keys = dataMap.keySet()
          logd("onDataChanged keys $keys")

          scope.launch {
            keys.forEach {
              val asset = dataMap.getAsset(it)
              try {
                saveFile(it, asset!!)
              } catch (e: Exception) {
                logd("Error: $e")
              }
            }
            dataStoreRepository.syncRecordingsComplete()
          }
        }
      }
    }
  }


  private suspend fun saveFile(name: String, asset: Asset) {
    val path = applicationContext.filesDir
    val file = File(path, name)


    val assetInputStream =
      Wearable.getDataClient(applicationContext).getFdForAsset(asset).await().inputStream
    assetInputStream.use { input ->
      Files.copy(input, file.toPath())
    }


  }
}