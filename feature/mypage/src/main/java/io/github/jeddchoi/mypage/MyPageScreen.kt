package io.github.jeddchoi.mypage

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import io.github.jeddchoi.actionlog.ActionLogRoute
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.mystatus.MyStatusRoute


@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyPageRoute(
    navTab: MyPageTab?,
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    MyPageScreen(
        navTab = navTab,
        shouldHandleReselection = shouldHandleReselection,
        onHandleReselection = onHandleReselection
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyPageScreen(
    navTab: MyPageTab?,
    shouldHandleReselection: Boolean,
    onHandleReselection: () -> Unit,
    myPageState: MyPageState = rememberMyPageState(),
) {


    LaunchedEffect(shouldHandleReselection) {
        Log.i("TAG", "navTab = $navTab / reselection = $shouldHandleReselection")
        if (shouldHandleReselection) {
            myPageState.toggleTab()
            onHandleReselection()
        } else {
            navTab?.let {
                myPageState.selectTab(it)
            }
        }
    }
    val selectedTab by myPageState.selectedTab.collectAsStateWithLifecycle(MyPageTab.MY_STATUS)

    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Remember a PagerState

        TabRow(selectedTabIndex = selectedTab.ordinal) {

            // Add tabs for all of our pages
            MyPageNavigation.myPageTabs.forEach { tab ->
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
                        myPageState.selectTab(tab)
                    }
                )
            }
        }

        HorizontalPager(
            count = MyPageTab.values().size,
            state = myPageState.pagerState,
            // Add 16.dp padding to 'center' the pages
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            // Our content for each page
            when (MyPageNavigation.myPageTabs[pageIndex]) {
                MyPageTab.MY_STATUS -> MyStatusRoute()
                MyPageTab.ACTION_LOG -> ActionLogRoute()
            }
        }

    }
}


@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun MyPageScreenPreview() {
    TheNewCafeTheme {
    }
}