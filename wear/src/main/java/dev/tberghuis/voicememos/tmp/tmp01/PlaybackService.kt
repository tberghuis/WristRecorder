package dev.tberghuis.voicememos.tmp.tmp01

import android.media.AudioTrack
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dev.tberghuis.voicememos.common.AudioController.Companion.CHANNELS_OUT
import dev.tberghuis.voicememos.common.AudioController.Companion.FORMAT
import dev.tberghuis.voicememos.common.AudioController.Companion.RECORDING_RATE

class PlaybackService : MediaSessionService() {
  private var mediaSession: MediaSession? = null

  @OptIn(UnstableApi::class)
  override fun onCreate() {
    super.onCreate()

    val player = ExoPlayer.Builder(this)
      .setMediaSourceFactory(MyMediaSourceFactory(DefaultMediaSourceFactory(this)))
      .build()
    mediaSession = MediaSession.Builder(this, player).build()
  }

  override fun onDestroy() {
    mediaSession?.run {
      player.release()
      release()
      mediaSession = null
    }
    super.onDestroy()
  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
    return mediaSession
  }
}