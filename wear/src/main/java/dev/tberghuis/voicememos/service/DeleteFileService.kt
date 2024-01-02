package dev.tberghuis.voicememos.service

import android.content.Context
import dev.tberghuis.voicememos.common.deleteFileCommon

class DeleteFileService(val appContext: Context,) {
    // todo dagger/hilt and invoke
  // doitwrong
  fun deleteFile(file: String) {
    deleteFileCommon(appContext, file)
  }
}