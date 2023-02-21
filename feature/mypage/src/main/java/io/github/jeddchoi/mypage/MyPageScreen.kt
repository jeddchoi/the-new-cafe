package io.github.jeddchoi.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.github.jeddchoi.actionlog.ActionLogRoute
import io.github.jeddchoi.designsystem.ui.theme.TheNewCafeTheme
import io.github.jeddchoi.mystatus.MyStatusRoute
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.launch


@Composable
fun MyPageRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val viewModel: MyPageViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyPageScreen(uiState)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyPageScreen(
    myPageUiState: UiState<MyPageUiStateData>
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when (myPageUiState) {
            UiState.Empty -> Text("EMPTY")
            is UiState.Error -> Text("ERROR : ${myPageUiState.exception.message}")
            is UiState.Loading -> Text("LOADING ${myPageUiState.data?.tabId}")
            is UiState.Success -> {
                val tabId = myPageUiState.data.tabId
                val pages = remember {
                    listOf("My Status", "Action Log")
                }

                val coroutineScope = rememberCoroutineScope()

                // Remember a PagerState
                val pagerState = rememberPagerState()

                LaunchedEffect(tabId) {
                    coroutineScope.launch {
                        if (tabId == "my_status") {
                            pagerState.animateScrollToPage(0)
                        } else {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                }

                TabRow(selectedTabIndex = pagerState.currentPage) {
                    // Add tabs for all of our pages
                    pages.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(
                                    title,
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 16.sp
                                )
                            },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                // Animate to the selected page when clicked
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        )
                    }
                }

                HorizontalPager(
                    count = pages.size,
                    state = pagerState,
                    // Add 16.dp padding to 'center' the pages
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    // Our content for each page
                    Surface(modifier = Modifier.fillMaxSize()) {
                        when (page) {
                            0 -> MyStatusRoute()
                            1 -> ActionLogRoute()
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun MyPageScreenPreview() {
    TheNewCafeTheme {
        MyPageScreen(myPageUiState = UiState.Success(MyPageUiStateData("my_status")))
    }
}