package io.github.jeddchoi.mypage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jeddchoi.actionlog.ActionLogRoute
import io.github.jeddchoi.mystatus.MyStatusRoute
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    selectedTab: MyPageNavigation.Tab = MyPageNavigation.Tab.MY_STATUS,
    pagerState: PagerState = rememberPagerState(),
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            // Add tabs for all of our pages
            MyPageNavigation.tabs.forEach { tab ->
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
                            pagerState.animateScrollToPage(selectedTab.ordinal)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            pageCount = MyPageNavigation.tabs.size,
            // Add 16.dp padding to 'center' the pages
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->

            // Our content for each page
            when (MyPageNavigation.tabs[pageIndex]) {
                MyPageNavigation.Tab.MY_STATUS -> MyStatusRoute()
                MyPageNavigation.Tab.ACTION_LOG -> ActionLogRoute()
            }
        }

    }
}