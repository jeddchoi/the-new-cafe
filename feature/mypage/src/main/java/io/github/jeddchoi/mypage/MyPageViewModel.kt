package io.github.jeddchoi.mypage

import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//
//class MyPageViewModel(
//    savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private val args = savedStateHandle.getStateFlow(tabIdArg, MyPageTab.MY_STATUS.name)
//    private val _uiState: Flow<MyPageState> = args.map {
//        MyPageState(it)
//    }
//
//
//    val uiState: StateFlow<UiState<MyPageState>>
//        get() = _uiState.map<MyPageState, UiState<MyPageState>> {
//            UiState.Success(it)
//        }.catch {
//            emit(UiState.Error(it))
//        }.stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5_000),
//            UiState.Loading()
//        )
//}

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

