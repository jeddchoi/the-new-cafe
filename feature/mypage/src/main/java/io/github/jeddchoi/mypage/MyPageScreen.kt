package io.github.jeddchoi.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.random.Random


@Composable
fun MyPageRoute() {



    MyPageScreen()
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyPageScreen() {
    val pages = remember {
        listOf("My Status", "Action Log")
    }
    val route = Random.nextBoolean()

    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)) {
        val coroutineScope = rememberCoroutineScope()

        // Remember a PagerState
        val pagerState = rememberPagerState()

        LaunchedEffect(route) {
            coroutineScope.launch {
                if (route) {
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
                    text = { Text(title, modifier = Modifier.padding(8.dp), fontSize = 16.sp) },
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

                if (page % 2 == 0) {
                    Even()
                } else {
                    Odd()
                }
            }
        }
    }

}


@Composable
fun Odd() {
    LazyColumn {
        items(50) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 72.dp)
                .padding(vertical = 8.dp)) {
                Text("Odd!")
            }

        }
    }
}

@Composable
fun Even() {
    LazyColumn {
        items(50) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 72.dp)
                .padding(vertical = 8.dp)) {
                Text("Even!")
            }
        }
    }
}


@Preview
@Composable
fun MyPageScreenPreview() {

}