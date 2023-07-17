package io.github.jeddchoi.mypage.history

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import io.github.jeddchoi.model.UserSessionHistory

@Composable
internal fun HistoryScreen(
    pagingHistories: LazyPagingItems<UserSessionHistory>,
    modifier: Modifier = Modifier,
    navigateToHistoryDetail: (String) -> Unit = {},
) {
    val refresh = pagingHistories.loadState.refresh
    val append = pagingHistories.loadState.append

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
    ) {
        items(pagingHistories.itemCount) { item ->
            pagingHistories[item]?.let { history ->
                UserSessionHistoryCard(
                    history = history,
                    modifier = Modifier
                        .clickable {
                            navigateToHistoryDetail(history.sessionId)
                        }
                        .fillMaxWidth()
                )
            }
        }
        item {
            Spacer(
                modifier = Modifier.height(200.dp)
            )
        }
    }

    pagingHistories.loadState.apply {
        when {
            refresh is LoadState.Loading -> CircularProgressIndicator()
            refresh is LoadState.Error -> Log.e("HistoryScreen", "refresh error ${refresh.error}")
            append is LoadState.Loading -> CircularProgressIndicator()
            append is LoadState.Error -> Log.e("HistoryScreen", "refresh error ${append.error}")
        }
    }
}


@Composable
fun UserSessionHistoryCard(
    history: UserSessionHistory,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = history.startTime.toString()
            )
        },
        supportingContent = {
            Text(
                text = history.sessionId
            )
        }
    )
}