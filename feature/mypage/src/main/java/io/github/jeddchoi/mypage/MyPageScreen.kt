@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.jeddchoi.mypage

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jeddchoi.mypage.history.HistoryScreen
import io.github.jeddchoi.mypage.session.SessionScreen
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen
import io.github.jeddchoi.ui.fullscreen.NotAuthenticatedScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyPageScreen(
    uiState: MyPageUiState,
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,
    selectTab: (MyPageTab) -> Unit = {},
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        MyPageTabRow(
            selectedTab = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            selectTab = selectTab
        )

        MyPageWithBottomSheet(
            uiState = uiState,
            selectedTab = selectedTab,
            selectTab = selectTab
        )
    }
}


@Composable
private fun MyPageTabRow(
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,
    selectTab: (MyPageTab) -> Unit = {}
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTab.ordinal,
    ) {
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
                selected = selectedTab == tab,
                onClick = {
                    selectTab(tab)
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun MyPageWithBottomSheet(
    uiState: MyPageUiState,
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,
    bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        rememberStandardBottomSheetState(skipHiddenState = true)
    ),
    selectTab: (MyPageTab) -> Unit = {},
) {
    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 128.dp,
        sheetContent = {
            ControlPanel()
        },
    ) { _ ->
        MyPageWithPager(
            uiState = uiState,
            modifier = modifier,
            selectedTab = selectedTab,
            selectTab = selectTab,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun MyPageWithPager(
    uiState: MyPageUiState,
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,
    pagerState: PagerState = rememberPagerState(
        initialPage = selectedTab.ordinal,
        initialPageOffsetFraction = 0f
    ) {
        MyPageTab.VALUES.size
    },
    selectTab: (MyPageTab) -> Unit = {},
) {

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }
    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.settledPage }.collect { page ->
            Log.d("Page change", "Page changed to $page")
            selectTab(MyPageTab.VALUES[page])
        }
    }

    HorizontalPager(
        state = pagerState,
    ) {
        // Our content for each page
        when (uiState) {
            MyPageUiState.InitialLoading -> {
                LoadingScreen(
                    modifier = modifier,
                )
            }

            MyPageUiState.NotAuthenticated -> {
                NotAuthenticatedScreen(
                    modifier = modifier,
                )
            }

            is MyPageUiState.Error -> {
                ErrorScreen(
                    exception = uiState.exception,
                    modifier = modifier,
                )
            }

            is MyPageUiState.Success -> {
                when (MyPageTab.VALUES[it]) {
                    MyPageTab.SESSION -> {
                        SessionScreen(
                            uiState.displayedUserSession,
                            modifier = modifier,
                        )
                    }

                    MyPageTab.HISTORY -> {
                        HistoryScreen()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MyPagePreview() {

}