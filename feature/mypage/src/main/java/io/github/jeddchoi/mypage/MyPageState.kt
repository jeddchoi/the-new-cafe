package io.github.jeddchoi.mypage

import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberMyPageState(
    pagerState: PagerState = rememberPagerState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): MyPageState {
    return remember(pagerState, coroutineScope) {
        MyPageState(pagerState, coroutineScope)
    }
}

@Stable
@ExperimentalPagerApi
class MyPageState(
    val pagerState: PagerState,
    private val coroutineScope: CoroutineScope
) {
    private val currentPageFlow =
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().map {
            myPageTabs[it]
        }


    val selectedTab: StateFlow<MyPageTab> = currentPageFlow.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5_000),
        MyPageTab.MY_STATUS
    )

    fun selectTab(destTab: MyPageTab) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(destTab.ordinal)
        }
    }

    fun toggleTab() {
        val destTab = when (selectedTab.value) {
            MyPageTab.MY_STATUS -> MyPageTab.ACTION_LOG
            MyPageTab.ACTION_LOG -> MyPageTab.MY_STATUS
        }

        selectTab(destTab)
    }
}

