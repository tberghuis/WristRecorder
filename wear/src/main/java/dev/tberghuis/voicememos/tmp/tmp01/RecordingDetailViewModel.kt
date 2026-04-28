package dev.tberghuis.voicememos.tmp.tmp01

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.service.DeleteFileService
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow

class RecordingDetailViewModel(
  private val application: Application,
  savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
  val deleteFileService = DeleteFileService(application)

  // file is navigation argument
  val file = savedStateHandle.get<String>("file")!!
  val showDeleteConfirm = mutableStateOf(false)
  val duration = calcDuration(application, file)


  private val player: MutableStateFlow<MediaController?> = MutableStateFlow(null)


  init {
    logd("savedStateHandle $savedStateHandle")
    createMediaSession(application)
  }


  private fun createMediaSession(context: Context) {
    Log.d("MediaViewModel", "createSessionToken called")
    val appContext = context.applicationContext

    // Explicitly start the service to ensure it can transition to foreground
//    val serviceIntent = Intent(appContext, PlaybackService::class.java)
//    appContext.startService(serviceIntent)

    val sessionToken =
      SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
    val controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
    controllerFuture.addListener(
      {
        try {
          Log.d("MediaViewModel", "MediaController connected")
          player.value = controllerFuture.get().also {
            it.addListener(object : Player.Listener {
              override fun onPlaybackStateChanged(state: Int) {
                Log.d("MediaViewModel", "Controller PlaybackState: $state")
                when (state) {
                  PlaybackState.STATE_PLAYING -> {
//                    playerState = "playing"
                  }

                  PlaybackState.STATE_STOPPED -> {
//                    playerState = "stopped"
                  }

                  else -> {}
                }
              }

              override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Log.e("MediaViewModel", "Controller PlayerError: ${error.message}", error)
              }

              // this did not work for some reason but onPlaybackStateChanged works
//              override fun onIsPlayingChanged(isPlaying: Boolean) {
//                super.onIsPlayingChanged(isPlaying)
//              }
            })
          }
        } catch (e: Exception) {
          Log.e("MediaViewModel", "Failed to connect MediaController", e)
        }
      },
      MoreExecutors.directExecutor()
    )
  }

  fun playRecording() {
    Log.d("MediaViewModel", "playSomething called. Player is null? ${player == null}")

    val dir = application.filesDir
    val f = File(dir, file)

    val media = MediaItem.Builder()
      .setMediaId("sample_audio_1")
      .setUri(f.toUri())
      .setMediaMetadata(
        MediaMetadata.Builder()
          .setTitle("Sample Audio")
          .setArtist("Sample Artist")
          .build()
      )
      .build()

    player.value?.let { p ->
      Log.d("MediaViewModel", "Setting media item and playing...")
      p.setMediaItem(media)
      p.prepare()
      p.play()
    }


  }


}