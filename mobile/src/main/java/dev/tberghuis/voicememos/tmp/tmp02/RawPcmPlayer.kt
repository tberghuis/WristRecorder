package dev.tberghuis.voicememos.tmp.tmp02

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

// todo refactor to SimplePlayer

@UnstableApi
class RawPcmPlayer(context: Context) {
  private val player: ExoPlayer = ExoPlayer.Builder(context)
    .setMediaSourceFactory(RawPcmMediaSourceFactory(DefaultMediaSourceFactory(context)))
    .build()

  var isPlayingUri by mutableStateOf<Uri?>(null)

  init {
    player.addListener(
      object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
          if (isPlaying) {
            // Active playback.
          } else {
            isPlayingUri = null
          }
        }
      }
    )
  }

  fun play(uri: Uri) {
    val mediaItem = MediaItem.fromUri(uri)
    player.setMediaItem(mediaItem)
    player.prepare()
    player.play()
    isPlayingUri = uri
  }

  fun stop() {
    player.stop()
  }

  fun release() {
    player.release()
  }
}