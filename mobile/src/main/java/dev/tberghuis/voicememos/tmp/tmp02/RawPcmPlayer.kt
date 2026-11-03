package dev.tberghuis.voicememos.tmp.tmp02

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@UnstableApi
class RawPcmPlayer(context: Context) {
  val player: ExoPlayer = ExoPlayer.Builder(context)
    .setMediaSourceFactory(RawPcmMediaSourceFactory(DefaultMediaSourceFactory(context)))
    .build()

  fun play(uri: Uri) {
    val mediaItem = MediaItem.fromUri(uri)
    player.setMediaItem(mediaItem)
    player.prepare()
    player.play()
  }

  fun release() {
    player.release()
  }
}