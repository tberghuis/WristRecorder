package dev.tberghuis.voicememos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.tberghuis.voicememos.page.RecordingList
import dev.tberghuis.voicememos.page.RecordingPage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
  navigateRecordingDetail: (String) -> Unit
) {
  val viewModel: HomeViewModel = hiltViewModel()
  // todo hoist this viewmodel, home?pagerIndex=n
  val pagerState = rememberPagerState()
  val pageIndicatorState: PageIndicatorState = remember {
    object : PageIndicatorState {
      override val pageOffset: Float
        get() = pagerState.currentPageOffset
      override val selectedPage: Int
        get() = pagerState.currentPage
      override val pageCount: Int
        get() = pagerState.pageCount
    }
  }

  Scaffold(modifier = Modifier.fillMaxSize(), pageIndicator = {
    HorizontalPageIndicator(
      pageIndicatorState = pageIndicatorState
    )
  }) {
    HorizontalPager(
      count = 2, state = pagerState
    ) { page ->
      when (page) {
        0 -> {
          RecordingPage(navigateRecordingDetail)
        }
        1 -> {
          RecordingList(navigateRecordingDetail)
        }
//        2 -> {
//          SettingsPage()
//        }
      }
    }
  }

  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }.collect { page ->
      if (page == 1) {
        viewModel.getRecordings()
      }
    }
  }
}

//@Composable
//fun SettingsPage() {
//  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//    Text("todo settings")
//  }
//}