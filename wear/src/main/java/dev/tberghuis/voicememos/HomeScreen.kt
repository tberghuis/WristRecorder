package dev.tberghuis.voicememos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import dev.tberghuis.voicememos.page.RecordingList
import dev.tberghuis.voicememos.page.RecordingPage
import dev.tberghuis.voicememos.page.SettingsPage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
  navigateRecordingDetail: (String) -> Unit
) {
  val viewModel: HomeViewModel = viewModel()

//  val pageCount = 3
//  val pagerState = rememberPagerState(pageCount = { pageCount })
  val pageIndicatorState: PageIndicatorState = remember {
    object : PageIndicatorState {
      override val pageOffset: Float
        get() = viewModel.pagerState.currentPageOffsetFraction
      override val selectedPage: Int
        get() = viewModel.pagerState.currentPage
      override val pageCount: Int
        get() = viewModel.pageCount
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    pageIndicator = {
      HorizontalPageIndicator(
        pageIndicatorState = pageIndicatorState
      )
    },
    timeText = {
      TimeText()
    },

    ) {
    HorizontalPager(
      state = viewModel.pagerState
    ) { page ->
      when (page) {
        0 -> {
          RecordingPage(navigateRecordingDetail)

//          if(pagerState.currentPage == 0){
//            BackButtonOverride()
//          }
          

        }

        1 -> {
          RecordingList(navigateRecordingDetail)
        }

        2 -> {
          SettingsPage()
        }
      }
    }
  }

  LaunchedEffect(viewModel.pagerState) {
    snapshotFlow { viewModel.pagerState.currentPage }.collect { page ->
      if (page == 1) {
        viewModel.getRecordings()
      }
    }
  }
}