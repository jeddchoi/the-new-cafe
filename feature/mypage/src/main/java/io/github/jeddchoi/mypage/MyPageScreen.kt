@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.jeddchoi.mypage

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.SeatFinderUserRequestType
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.SessionTimer
import io.github.jeddchoi.model.UserSessionHistory
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.mypage.history.HistoryScreen
import io.github.jeddchoi.mypage.session.SessionScreen
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen
import io.github.jeddchoi.ui.fullscreen.NotAuthenticatedScreen
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyPageScreen(
    uiState: MyPageUiState,
    pagingHistories: LazyPagingItems<UserSessionHistory>,
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,
    selectTab: (MyPageTab) -> Unit = {},
    sendRequest: (SeatFinderUserRequestType, Int?, Long?) -> Unit = { _, _, _ -> },
    navigateToHistoryDetail: (String) -> Unit = {},
    navigateToSignIn: () -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        MyPageTabRow(
            selectedTab = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            selectTab = selectTab
        )

        MyPageWithBottomSheet(
            uiState = uiState,
            pagingHistories = pagingHistories,
            selectedTab = selectedTab,
            selectTab = selectTab,
            sendRequest = sendRequest,
            navigateToHistoryDetail = navigateToHistoryDetail,
            navigateToSignIn = navigateToSignIn
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
    pagingHistories: LazyPagingItems<UserSessionHistory>,
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,

    selectTab: (MyPageTab) -> Unit = {},
    sendRequest: (SeatFinderUserRequestType, Int?, Long?) -> Unit = { _, _, _ -> },
    navigateToHistoryDetail: (String) -> Unit = {},
    navigateToSignIn: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    when (uiState) {
        MyPageUiState.InitialLoading -> {
            LoadingScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        MyPageUiState.NotAuthenticated -> {
            NotAuthenticatedScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToSignIn = navigateToSignIn,
            )
        }

        is MyPageUiState.Success -> {
            val bottomSheetScaffoldState: BottomSheetScaffoldState =
                rememberBottomSheetScaffoldState(
                    rememberStandardBottomSheetState(
                        skipHiddenState = false,
                        initialValue = if (uiState.displayedUserSession is DisplayedUserSession.UsingSeat) SheetValue.PartiallyExpanded else SheetValue.Hidden
                    )
                )
            BottomSheetScaffold(
                modifier = modifier,
                scaffoldState = bottomSheetScaffoldState,
                sheetPeekHeight = 128.dp,
                sheetContent = {
                    ControlPanel(
                        displayedUserSession = uiState.displayedUserSession,
                        sendRequest = sendRequest,
                        expandPartially = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    )
                },
            ) { _ ->
                MyPageWithPager(
                    displayedUserSession = uiState.displayedUserSession,
                    pagingHistories = pagingHistories,
                    modifier = Modifier.fillMaxSize(),
                    selectedTab = selectedTab,
                    selectTab = selectTab,
                    navigateToHistoryDetail = navigateToHistoryDetail,
                )
            }
            LaunchedEffect(uiState.displayedUserSession.state) {
                if (uiState.displayedUserSession is DisplayedUserSession.UsingSeat && !bottomSheetScaffoldState.bottomSheetState.isVisible) {
                    bottomSheetScaffoldState.bottomSheetState.show()
                } else if (uiState.displayedUserSession !is DisplayedUserSession.UsingSeat && bottomSheetScaffoldState.bottomSheetState.isVisible) {
                    bottomSheetScaffoldState.bottomSheetState.hide()
                }
            }
        }

        is MyPageUiState.Error -> {
            ErrorScreen(
                exception = uiState.exception,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun MyPageWithPager(
    displayedUserSession: DisplayedUserSession,
    pagingHistories: LazyPagingItems<UserSessionHistory>,
    modifier: Modifier = Modifier,
    selectedTab: MyPageTab = MyPageTab.SESSION,
    pagerState: PagerState = rememberPagerState(
        initialPage = selectedTab.ordinal,
        initialPageOffsetFraction = 0f
    ) {
        MyPageTab.VALUES.size
    },
    selectTab: (MyPageTab) -> Unit = {},
    navigateToHistoryDetail: (String) -> Unit = {},
) {

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }
    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.settledPage }.collect { page ->
            Timber.v("Page changed to $page")
            selectTab(MyPageTab.VALUES[page])
        }
    }


    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) {
        // Our content for each page
        when (MyPageTab.VALUES[it]) {
            MyPageTab.SESSION -> {
                SessionScreen(
                    displayedUserSession = displayedUserSession,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            MyPageTab.HISTORY -> {
                HistoryScreen(
                    modifier = Modifier.fillMaxSize(),
                    pagingHistories = pagingHistories,
                    navigateToHistoryDetail = navigateToHistoryDetail,
                    currentSession = displayedUserSession
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MyPagePreview() {
    TheNewCafeTheme {
        Surface {
            val viewModel: MyPageViewModel = hiltViewModel()
            val pagingHistories = viewModel.histories.collectAsLazyPagingItems()
            MyPageScreen(
                uiState = MyPageUiState.Success(
                    displayedUserSession = DisplayedUserSession.UsingSeat(
                        "",
                        SessionTimer(),
                        SessionTimer(),
                        hasFailure = false,
                        seatPosition = SeatPosition(),
                        resultStateAfterCurrentState = null,
                        state = UserStateType.Occupied
                    )
                ),
                pagingHistories = pagingHistories
            )
        }
    }

}