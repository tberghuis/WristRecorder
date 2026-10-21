package dev.tberghuis.voicememos.tmp.tmp01

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class SimplePlayer(context: Context) {
  val player: ExoPlayer = ExoPlayer.Builder(context).build()

  fun play(uri: android.net.Uri) {
    val mediaItem = MediaItem.fromUri(uri)
    player.setMediaItem(mediaItem)
    player.prepare()
    player.play()
  }

  fun release() {
    player.release()
  }
}