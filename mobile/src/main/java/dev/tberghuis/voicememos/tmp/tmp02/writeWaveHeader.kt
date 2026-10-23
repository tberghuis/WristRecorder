package dev.tberghuis.voicememos.tmp.tmp02

import java.io.OutputStream

fun writeWaveHeader(
  out: OutputStream,
  totalAudioLen: Long,
  longSampleRate: Long,
  channels: Int,
  byteRate: Long
) {
  val totalDataLen = totalAudioLen + 36
  val bitsPerSample = 16.toShort()
  val blockAlign = (channels * 16 / 8).toShort()

  val header = ByteArray(44)
  header[0] = 'R'.code.toByte() // RIFF/WAVE header
  header[1] = 'I'.code.toByte()
  header[2] = 'F'.code.toByte()
  header[3] = 'F'.code.toByte()
  header[4] = (totalDataLen and 0xff).toByte()
  header[5] = ((totalDataLen shr 8) and 0xff).toByte()
  header[6] = ((totalDataLen shr 16) and 0xff).toByte()
  header[7] = ((totalDataLen shr 24) and 0xff).toByte()
  header[8] = 'W'.code.toByte()
  header[9] = 'A'.code.toByte()
  header[10] = 'V'.code.toByte()
  header[11] = 'E'.code.toByte()
  header[12] = 'f'.code.toByte() // 'fmt ' chunk
  header[13] = 'm'.code.toByte()
  header[14] = 't'.code.toByte()
  header[15] = ' '.code.toByte()
  header[16] = 16 // 4 bytes: size of 'fmt ' chunk
  header[17] = 0
  header[18] = 0
  header[19] = 0
  header[20] = 1.toByte() // format = 1 (PCM)
  header[21] = 0
  header[22] = channels.toByte()
  header[23] = 0
  header[24] = (longSampleRate and 0xff).toByte()
  header[25] = ((longSampleRate shr 8) and 0xff).toByte()
  header[26] = ((longSampleRate shr 16) and 0xff).toByte()
  header[27] = ((longSampleRate shr 24) and 0xff).toByte()
  header[28] = (byteRate and 0xff).toByte()
  header[29] = ((byteRate shr 8) and 0xff).toByte()
  header[30] = ((byteRate shr 16) and 0xff).toByte()
  header[31] = ((byteRate shr 24) and 0xff).toByte()
  header[32] = blockAlign.toByte()
  header[33] = 0
  header[34] = bitsPerSample.toByte()
  header[35] = 0
  header[36] = 'd'.code.toByte() // 'data' chunk
  header[37] = 'a'.code.toByte()
  header[38] = 't'.code.toByte()
  header[39] = 'a'.code.toByte()
  header[40] = (totalAudioLen and 0xff).toByte()
  header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
  header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
  header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

  out.write(header, 0, 44)
}
