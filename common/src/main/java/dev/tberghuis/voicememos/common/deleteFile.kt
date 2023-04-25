package dev.tberghuis.voicememos.common

import android.content.Context
import java.io.File

// doitwrong
fun deleteFileCommon(context: Context, file: String) {
  logd("deleteFile")
  val dir = context.filesDir
  val f = File(dir, file)
  if (f.delete()) {
    logd("file deleted")
  } else {
    logd("file not deleted")
  }
}