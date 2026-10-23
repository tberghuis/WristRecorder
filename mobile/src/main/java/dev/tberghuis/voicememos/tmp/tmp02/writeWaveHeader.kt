package dev.tberghuis.voicememos.tmp.tmp02

import java.io.OutputStream

fun writeWaveHeader(
  out: OutputStream,
  totalAudioLen: Long,
//  longSampleRate: Long,
//  channels: Int,
//  byteRate: Long
) {
//  val totalDataLen = totalAudioLen + 36
//  val bitsPerSample = 16.toShort()
//  val blockAlign = (channels * 16 / 8).toShort()

  val header = createWavHeader(totalAudioLen)

  out.write(header, 0, 44)
}
