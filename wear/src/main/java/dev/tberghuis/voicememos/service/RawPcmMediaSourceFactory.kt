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


private fun createWavHeader(pcmAudioSize: Long): ByteArray {
  val header = ByteArray(44)
  val totalAudioLen = pcmAudioSize + 36
  val sampleRate = RECORDING_RATE.toLong()
  val channels = 1
  val byteRate = sampleRate * channels * 2

  header[0] = 'R'.code.toByte()
  header[1] = 'I'.code.toByte()
  header[2] = 'F'.code.toByte()
  header[3] = 'F'.code.toByte()
  header[4] = (totalAudioLen and 0xff).toByte()
  header[5] = ((totalAudioLen shr 8) and 0xff).toByte()
  header[6] = ((totalAudioLen shr 16) and 0xff).toByte()
  header[7] = ((totalAudioLen shr 24) and 0xff).toByte()
  header[8] = 'W'.code.toByte()
  header[9] = 'A'.code.toByte()
  header[10] = 'V'.code.toByte()
  header[11] = 'E'.code.toByte()
  header[12] = 'f'.code.toByte()
  header[13] = 'm'.code.toByte()
  header[14] = 't'.code.toByte()
  header[15] = ' '.code.toByte()
  header[16] = 16
  header[17] = 0
  header[18] = 0
  header[19] = 0
  header[20] = 1
  header[21] = 0
  header[22] = channels.toByte()
  header[23] = 0
  header[24] = (sampleRate and 0xff).toByte()
  header[25] = ((sampleRate shr 8) and 0xff).toByte()
  header[26] = ((sampleRate shr 16) and 0xff).toByte()
  header[27] = ((sampleRate shr 24) and 0xff).toByte()
  header[28] = (byteRate and 0xff).toByte()
  header[29] = ((byteRate shr 8) and 0xff).toByte()
  header[30] = ((byteRate shr 16) and 0xff).toByte()
  header[31] = ((byteRate shr 24) and 0xff).toByte()
  header[32] = (channels * 2).toByte()
  header[33] = 0
  header[34] = 16
  header[35] = 0
  header[36] = 'd'.code.toByte()
  header[37] = 'a'.code.toByte()
  header[38] = 't'.code.toByte()
  header[39] = 'a'.code.toByte()
  header[40] = (pcmAudioSize and 0xff).toByte()
  header[41] = ((pcmAudioSize shr 8) and 0xff).toByte()
  header[42] = ((pcmAudioSize shr 16) and 0xff).toByte()
  header[43] = ((pcmAudioSize shr 24) and 0xff).toByte()
  return header
}


