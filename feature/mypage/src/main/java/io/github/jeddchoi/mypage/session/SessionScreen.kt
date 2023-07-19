package io.github.jeddchoi.mypage.session

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.mypage.R
import io.github.jeddchoi.ui.fullscreen.PlaceholderScreen
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun SessionScreen(
    displayedUserSession: DisplayedUserSession,
    modifier: Modifier = Modifier
) {

    if (displayedUserSession is DisplayedUserSession.UsingSeat) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 200.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SessionTimerCircularIndicators(
                displayedUserSession,
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp),
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = displayedUserSession.stateName.asString(),
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.user_session_state)
                    )
                }
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(id = if (displayedUserSession.hasFailure) R.string.has_error else R.string.good),
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.user_session_state_check)
                    )
                }
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = displayedUserSession.seatPosition.toString(),
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.user_session_seat_position)
                    )
                }
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = displayedUserSession.resultStateAfterCurrentState?.name
                            ?: "-",
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.user_session_state_after_timeout)
                    )
                }
            )

            SessionTimerItems(
                title = UiText.StringResource(R.string.user_session_overall_timer),
                sessionTimer = displayedUserSession.sessionTimer
            )
            SessionTimerItems(
                title = UiText.StringResource(R.string.user_session_current_state_timer),
                sessionTimer = displayedUserSession.currentStateTimer
            )
        }

    } else {
        PlaceholderScreen(UiText.StringResource(R.string.user_session_none))
    }

}

@Composable
private fun SessionTimerCircularIndicators(
    displayedUserSession: DisplayedUserSession.UsingSeat,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val innerProgress = remember(displayedUserSession) {
            derivedStateOf {
                val value =
                    displayedUserSession.currentStateTimer.progress(true)
                        ?: 0f
                value
            }
        }

        val outerProgress = remember(displayedUserSession) {
            derivedStateOf {
                val value =
                    displayedUserSession.sessionTimer.progress(true)
                        ?: 0f
                value
            }
        }

        CircularProgressIndicator(
            progress = innerProgress.value,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxSize(0.5f),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 40.dp,
        )
        CircularProgressIndicator(
            progress = outerProgress.value,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxSize(1f),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 24.dp
        )
    }
}


@Composable
private fun ColumnScope.SessionTimerItems(
    title: UiText,
    sessionTimer: SessionTimer,
) {
    Divider()
    Text(
        text = title.asString(),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        textAlign = TextAlign.Start,
    )

    ListItem(
        headlineContent = {
            Text(
                text = sessionTimer.startTime.toString(),
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.session_timer_start_time)
            )
        }
    )

    ListItem(
        headlineContent = {
            Text(
                text = sessionTimer.endTime?.toString() ?: "-",
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.session_timer_end_time)
            )
        }
    )

    ListItem(
        headlineContent = {
            Text(
                text = sessionTimer.totalTime?.toString() ?: "-",
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.session_timer_total_time)
            )
        }
    )

    ListItem(
        headlineContent = {
            Text(
                text = sessionTimer.elapsedTime.toString(),
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.session_timer_elapsed_time)
            )
        }
    )

    ListItem(
        headlineContent = {
            Text(
                text = sessionTimer.remainingTime?.toString() ?: "-",
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.session_timer_remaining_time)
            )
        }
    )
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
fun SessionScreenPreview() {
    TheNewCafeTheme {
        Surface {
            SessionScreen(
                displayedUserSession = DisplayedUserSession.UsingSeat.Away(
                    SessionTimer(
                        startTime = Clock.System.now().minus(10.minutes),
                        endTime = Clock.System.now().plus(10.minutes),
                        elapsedTime = 10.minutes,
                        remainingTime = 10.minutes,
                        totalTime = 20.minutes,
                    ),
                    SessionTimer(
                        startTime = Clock.System.now().minus(5.minutes),
                        endTime = Clock.System.now().plus(10.minutes),
                        elapsedTime = 5.minutes,
                        remainingTime = 10.minutes,
                        totalTime = 15.minutes,
                    ),
                    hasFailure = true,
                    seatPosition = SeatPosition(),
                    resultStateAfterCurrentState = null,
                )
            )
        }
    }
}