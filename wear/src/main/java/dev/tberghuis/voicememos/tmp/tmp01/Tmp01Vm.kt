package dev.tberghuis.voicememos.tmp.tmp01

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.common.util.concurrent.MoreExecutors

class Tmp01Vm(application: Application) : AndroidViewModel(application) {
  private val player: MutableStateFlow<MediaController?> = MutableStateFlow(null)

  init {
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
}