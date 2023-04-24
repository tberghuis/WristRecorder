package dev.tberghuis.voicememos.common

import android.content.Context
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlin.math.roundToInt

fun formatTimestampFromFilename(file: String): String {
  // should learn regex
  val timestamp = file.split("_")[1].split(".")[0].toLong()
  val date = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
  )
//  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  val formatter = DateTimeFormatter.ofPattern("EEE, d MMM HH:mm")
  return date.format(formatter)
}


fun calcDuration(context: Context, file: String): Int {
  val f = File(context.filesDir, file)
  // https://stackoverflow.com/questions/4715085/return-the-length-in-seconds-of-a-track-to-be-played-using-audiotrack
  val duration = f.length().toFloat() / AudioController.RECORDING_RATE / 2
  return duration.roundToInt()
}