package dev.tberghuis.voicememos.presentation

import android.content.Context
import dev.tberghuis.voicememos.service.AudioConstants
import dev.tberghuis.voicememos.service.AudioRecordService
import java.io.File
import kotlin.math.roundToInt


fun calcDuration(context: Context, file: String): Int {
  val f = File(context.filesDir, file)
  // https://stackoverflow.com/questions/4715085/return-the-length-in-seconds-of-a-track-to-be-played-using-audiotrack
  val duration = f.length().toFloat() / AudioConstants.RECORDING_RATE / 2
  return duration.roundToInt()
}