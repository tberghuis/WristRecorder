package dev.tberghuis.voicememos

fun formatSecondsToMinutesAndSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}