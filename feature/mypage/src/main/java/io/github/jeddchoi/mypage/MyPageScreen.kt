@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.jeddchoi.mypage

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.mypage.history.HistoryScreen
import io.github.jeddchoi.mypage.session.SessionScreen
import io.github.jeddchoi.ui.feature.LoadingScreen
import io.github.jeddchoi.ui.feature.PlaceholderScreen
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MyPageScreen(
    navigateTab: MyPageTab,
    uiState: MyPageUiState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(navigateTab)
    }

    Column(
        modifier.padding(8.dp)
    ) {
        MyPageTabRow(selectedTab) {
            selectedTab = it
        }

        MyPageContent(
            uiState = uiState,
            selectedTab = selectedTab,
            coroutineScope = coroutineScope,
            onTabChanged = {
                selectedTab = it
            })

    }

}


@Composable
private fun MyPageTabRow(
    selectedTab: MyPageTab,
    onSelectTab: (MyPageTab) -> Unit
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
                    onSelectTab(tab)
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun MyPageContent(
    uiState: MyPageUiState,
    coroutineScope: CoroutineScope,
    selectedTab: MyPageTab,
    onTabChanged: (MyPageTab) -> Unit,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        rememberStandardBottomSheetState(skipHiddenState = true)
    ),
    pagerState: PagerState = rememberPagerState(
        initialPage = selectedTab.ordinal,
        initialPageOffsetFraction = 0f
    ) {
        MyPageTab.VALUES.size
    },
) {
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }
    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.settledPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            Log.d("Page change", "Page changed to $page")
            onTabChanged(MyPageTab.VALUES[page])
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,
        sheetContent = {
            ControlPanel(
                scaffoldState,
                coroutineScope,
                emptyList()
            )
        },
    ) { _ ->
        HorizontalPager(
            modifier = Modifier.border(4.dp, MaterialTheme.colorScheme.primary),
            state = pagerState,
        ) {
            // Our content for each page
            when (MyPageTab.VALUES[it]) {
                MyPageTab.SESSION -> {
                    when (uiState) {
                        MyPageUiState.InitialLoading -> LoadingScreen()
                        MyPageUiState.NotAuthenticated -> PlaceholderScreen(title = UiText.StringResource(R.string.not_authenticated))
                        is MyPageUiState.Error -> PlaceholderScreen(title = UiText.StringResource(R.string.error))
                        is MyPageUiState.Success -> {
                            SessionScreen(uiState.displayedUserSession)
                        }
                    }
                }
                MyPageTab.HISTORY -> {
                    when (uiState) {
                        MyPageUiState.InitialLoading -> LoadingScreen()
                        MyPageUiState.NotAuthenticated -> PlaceholderScreen(title = UiText.StringResource(R.string.not_authenticated))
                        is MyPageUiState.Error -> PlaceholderScreen(title = UiText.StringResource(R.string.error))
                        is MyPageUiState.Success -> {
                            HistoryScreen()
                        }
                    }
                }
            }
        }
    }


}

@Preview
@Composable
fun MyPagePreview() {

}