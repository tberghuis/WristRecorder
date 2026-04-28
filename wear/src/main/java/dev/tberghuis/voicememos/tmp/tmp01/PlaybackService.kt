package dev.tberghuis.voicememos.tmp.tmp01

import android.media.AudioTrack
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dev.tberghuis.voicememos.common.AudioController.Companion.CHANNELS_OUT
import dev.tberghuis.voicememos.common.AudioController.Companion.FORMAT
import dev.tberghuis.voicememos.common.AudioController.Companion.RECORDING_RATE

class PlaybackService : MediaSessionService() {
  private var mediaSession: MediaSession? = null

  override fun onCreate() {
    super.onCreate()


    val intSize = AudioTrack.getMinBufferSize(RECORDING_RATE, CHANNELS_OUT, FORMAT)


    val player = ExoPlayer.Builder(this)
//      .setAudioAttributes(
//        AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//          .setUsage(AudioAttributes.USAGE_MEDIA).build(), true
//      )
//.setAudioFormat(intSize)
      
      
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