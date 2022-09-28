package dev.tberghuis.voicememos.screen

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tberghuis.voicememos.presentation.calcDuration
import dev.tberghuis.voicememos.service.AudioRecordService
import dev.tberghuis.voicememos.service.AudioTrackService
import dev.tberghuis.voicememos.service.DeleteFileService
import dev.tberghuis.voicememos.util.logd
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class RecordingDetailViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  val audioTrackService: AudioTrackService,
  @ApplicationContext val appContext: Context,
  val deleteFileService: DeleteFileService
) : ViewModel() {
  val willitblend = "willitblend"

  // file is navigation argument
  val file = savedStateHandle.get<String>("file")!!


  val showDeleteConfirm = mutableStateOf(false)


  // doitwrong
  val duration = calcDuration(appContext, file)

  init {
    logd("savedStateHandle $savedStateHandle")
  }



}