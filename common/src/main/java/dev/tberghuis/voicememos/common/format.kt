package dev.tberghuis.voicememos.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

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