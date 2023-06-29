@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.jeddchoi.mypage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jeddchoi.mypage.history.HistoryScreen
import io.github.jeddchoi.mypage.session.SessionScreen
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MyPageScreen(
    modifier: Modifier = Modifier,
    navigateTab: MyPageTab,
    pagerState: PagerState = rememberPagerState(),
    uiState: UiState<MyPageUiStateData>,
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by rememberSaveable {
        mutableStateOf(navigateTab)
    }

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            selectedTab = MyPageTab.VALUES[page]
        }
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        rememberStandardBottomSheetState(skipHiddenState = true)
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,

        sheetContent = {
            ControlPanel(
                scaffoldState,
                scope,
                uiState
            )
        }) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                // Add tabs for all of our pages
                MyPageTab.VALUES.forEach { tab ->
                    Tab(
                        text = {
                            Text(
                                stringResource(tab.titleId),
                                modifier = Modifier.padding(8.dp),
                                fontSize = 16.sp
                            )
                        },
                        selected = selectedTab.ordinal == tab.ordinal,
                        onClick = {
                            // Animate to the selected page when clicked
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(tab.ordinal)
                            }
                        }
                    )
                }
            }
            // Our content for each page
            HorizontalPager(
                state = pagerState,
                pageCount = MyPageTab.VALUES.size,
                // Add 16.dp padding to 'center' the pages
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { pageIndex ->

                // Our content for each page
                when (MyPageTab.VALUES[pageIndex]) {
                    MyPageTab.Session -> SessionScreen()
                    MyPageTab.History -> HistoryScreen()
                }
            }

        }
    }

}


@Preview
@Composable
fun MyPagePreview() {

}