package dev.tberghuis.voicememos.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tberghuis.voicememos.common.deleteFileCommon
import javax.inject.Inject

class DeleteFileService
@Inject constructor(
  @ApplicationContext val appContext: Context,
) {
  // todo dagger/hilt and invoke
  // doitwrong
  fun deleteFile(file: String) {
    deleteFileCommon(appContext, file)
  }
}