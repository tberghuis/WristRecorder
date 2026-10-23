package dev.tberghuis.voicememos.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

//    wristrecorder_1792453993535 (1).wav
//fun String.toRecordingEntryTitle(): String {
//  val timestamp = split("_", " ", ".")[1].toLong()
//  val date = LocalDateTime.ofInstant(
//    Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
//  )
//  val formatter = DateTimeFormatter.ofPattern("EEE, d MMM HH:mm")
//  return date.format(formatter)
//}


fun getFormattedTimestamp(filename: String): String? {
  val regex = """wristrecorder_(?<timestamp>\d+)""".toRegex()
  val matchResult = regex.find(filename) ?: return null
  try {
    val timestamp = matchResult.groups["timestamp"]?.value?.toLong() ?: return null
    val date = LocalDateTime.ofInstant(
      Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
    )
    val formatter = DateTimeFormatter.ofPattern("EEE, d MMM HH:mm")
    return date.format(formatter)
  } catch (e: Exception) {
    return null
  }
}



