package dev.tberghuis.voicememos.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tberghuis.voicememos.util.logd
import java.io.File
import javax.inject.Inject


// doitwrong

class DeleteFileService
@Inject constructor(
  @ApplicationContext val appContext: Context,
) {
  // todo dagger/hilt and invoke

  fun deleteFile(file: String) {

    logd("deleteFile")


    val dir = appContext.filesDir

    val f = File(dir, file)



    if (f.delete()) {
      logd("file deleted")
    } else {
      logd("file not deleted")
    }

  }

}

// doitwrong for now
