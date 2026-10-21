package dev.tberghuis.voicememos.tmp.tmp01

import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.flow.MutableStateFlow
import android.content.Context
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedRecordingsVmc(
  private val context: Context,
  private val scope: CoroutineScope,
) {

  val sharedRecordingsList = MutableStateFlow(listOf<SharedRecording>())

  fun refreshSharedList() {
    logd("refreshSharedList")

    scope.launch(IO) {
      sharedRecordingsList.value = getListSharedRecordings(context)

      logd("sharedRecordingsList ${sharedRecordingsList.value}")
    }

  }


}

data class SharedRecording(
  val id: Long,
  val displayName: String,
  val relativePath: String,
  val uri: android.net.Uri
)


suspend fun getListSharedRecordings(context: Context): List<SharedRecording> {
  return withContext(IO) {
    val recordings = mutableListOf<SharedRecording>()

    val collectionUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    // https://developer.android.com/reference/android/provider/MediaStore.MediaColumns
    // do I need DURATION
    val projection = arrayOf(
      MediaStore.Audio.Media._ID,
      MediaStore.Audio.Media.DISPLAY_NAME,
      MediaStore.Audio.Media.RELATIVE_PATH,
      MediaStore.Audio.Media.MIME_TYPE,
      MediaStore.Audio.Media.DATE_ADDED
    )

    val selection = "${MediaStore.Audio.Media.RELATIVE_PATH} = ?"
    val selectionArgs = arrayOf("$WR_RELATIVE_PATH/")
    val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} ASC"

    context.contentResolver.query(
      collectionUri,
      projection,
      selection,
      selectionArgs,
      sortOrder
    )?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
      val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
      val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)

      while (cursor.moveToNext()) {
        val id = cursor.getLong(idColumn)
        val displayName = cursor.getString(nameColumn)
        val relativePath = cursor.getString(relativePathColumn)
        val itemUri = ContentUris.withAppendedId(collectionUri, id)

        recordings.add(
          SharedRecording(
            id = id,
            displayName = displayName,
            relativePath = relativePath,
            uri = itemUri
          )
        )
      }
    }

    println("recordings $recordings")
    recordings
  }
}

