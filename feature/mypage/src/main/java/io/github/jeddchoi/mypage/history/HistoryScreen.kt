package io.github.jeddchoi.mypage.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserSessionHistory
import io.github.jeddchoi.mypage.R
import io.github.jeddchoi.ui.fullscreen.EmptyResultScreen
import kotlinx.datetime.Instant
import timber.log.Timber

@Composable
internal fun HistoryScreen(
    pagingHistories: LazyPagingItems<UserSessionHistory>,
    currentSession: DisplayedUserSession?,
    modifier: Modifier = Modifier,
    navigateToHistoryDetail: (String) -> Unit = {},
) {
    val refresh = pagingHistories.loadState.refresh
    val prepend = pagingHistories.loadState.prepend

    LaunchedEffect(currentSession?.state) {
        currentSession?.let {
            pagingHistories.refresh()
        }
    }

    if (pagingHistories.itemCount == 0 && (currentSession == null || currentSession == DisplayedUserSession.None) ) {
        EmptyResultScreen(
            subject = UiText.StringResource(R.string.user_session_history),
            modifier = modifier,
        )
    } else {
        LazyColumn(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (currentSession is DisplayedUserSession.UsingSeat) {
                item {
                    UserSessionHistoryCard(
                        startTime = currentSession.sessionTimer.startTime,
                        endTime = currentSession.sessionTimer.endTime,
                        seatPosition = currentSession.seatPosition,
                        sessionId = currentSession.sessionId,
                        hasFailure = currentSession.hasFailure,
                        normalContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable {
                                navigateToHistoryDetail(currentSession.sessionId)
                            }
                            .fillMaxWidth()
                    )
                }
            }

            items(pagingHistories.itemCount) { itemIndex ->
                pagingHistories[pagingHistories.itemCount - itemIndex - 1]?.let { history ->
                    UserSessionHistoryCard(
                        startTime = history.startTime,
                        endTime = history.endTime,
                        seatPosition = history.seatPosition,
                        sessionId = history.sessionId,
                        hasFailure = history.hasFailure,
                        modifier = Modifier
                            .clickable {
                                navigateToHistoryDetail(history.sessionId)
                            }
                            .fillMaxWidth()
                    )
                }
            }

            pagingHistories.loadState.apply {
                when {
                    refresh is LoadState.Loading -> item { CircularProgressIndicator() }
                    prepend is LoadState.Loading -> item { CircularProgressIndicator() }

                    refresh is LoadState.Error -> Timber.e(refresh.error)
                    prepend is LoadState.Error -> Timber.e(prepend.error)
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(200.dp)
                )
            }
        }
    }
}


@Composable
fun UserSessionHistoryCard(
    modifier: Modifier = Modifier,
    startTime: Instant = Instant.DISTANT_PAST,
    endTime: Instant? = Instant.DISTANT_FUTURE,
    seatPosition: SeatPosition = SeatPosition(),
    sessionId: String = "",
    hasFailure: Boolean = false,
    normalContainerColor: Color = MaterialTheme.colorScheme.background
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = if (hasFailure.not()) normalContainerColor else MaterialTheme.colorScheme.errorContainer
        ),
        modifier = modifier,
        headlineContent = {
            Text(
                text = "$startTime ~ $endTime",
            )
        },
        supportingContent = {
            Text(
                text = seatPosition.toString(),
            )
        },
        overlineContent = {
            Text(
                text = sessionId
            )
        }
    )
}