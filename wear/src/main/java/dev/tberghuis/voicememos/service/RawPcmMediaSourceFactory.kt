package dev.tberghuis.voicememos.service

import android.net.Uri
import androidx.core.net.toFile
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import dev.tberghuis.voicememos.common.AudioController.Companion.RECORDING_RATE
import dev.tberghuis.voicememos.common.createWavHeader

// AI came up with this solution to play raw PCM files with ExoPlayer
@UnstableApi
class RawPcmMediaSourceFactory(val defaultFactory: DefaultMediaSourceFactory) : MediaSource.Factory {


  override fun setDrmSessionManagerProvider(
    drmSessionManagerProvider: DrmSessionManagerProvider
  ): MediaSource.Factory {
    defaultFactory.setDrmSessionManagerProvider(drmSessionManagerProvider)
    return this
  }

  override fun setLoadErrorHandlingPolicy(
    loadErrorHandlingPolicy: LoadErrorHandlingPolicy
  ): MediaSource.Factory {
    defaultFactory.setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
    return this
  }

  override fun getSupportedTypes(): IntArray {
    return defaultFactory.supportedTypes
  }

  override fun createMediaSource(mediaItem: MediaItem): MediaSource {
    // read from raw PCM file
    val file =
      mediaItem.localConfiguration?.uri?.toFile() ?: return defaultFactory.createMediaSource(
        mediaItem
      )
    val header = createWavHeader(file.length())

    val dataSourceFactory = DataSource.Factory {
      val fileDataSource = FileDataSource()
      object : DataSource {
        private var headerBytesRead = 0

        override fun addTransferListener(transferListener: TransferListener) {
          fileDataSource.addTransferListener(transferListener)
        }

        override fun open(dataSpec: DataSpec): Long {
          fileDataSource.open(DataSpec(Uri.fromFile(file)))
          return header.size + file.length()
        }

        override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
          if (headerBytesRead < header.size) {
            val bytesToRead = minOf(length, header.size - headerBytesRead)
            System.arraycopy(header, headerBytesRead, buffer, offset, bytesToRead)
            headerBytesRead += bytesToRead
            return bytesToRead
          }
          return fileDataSource.read(buffer, offset, length)
        }

        override fun getUri(): Uri? = Uri.fromFile(file)

        override fun close() {
          fileDataSource.close()
        }
      }
    }





    return ProgressiveMediaSource.Factory(dataSourceFactory)
      .createMediaSource(MediaItem.fromUri(Uri.fromFile(file)))
  }
}