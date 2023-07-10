package io.github.jeddchoi.order.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.model.Store
import io.github.jeddchoi.order.R
import io.github.jeddchoi.ui.component.ComponentWithBottomButtons
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.feature.EmptyResultScreen
import io.github.jeddchoi.ui.feature.ErrorScreen
import io.github.jeddchoi.ui.feature.LoadingScreen

@Composable
internal fun StoreScreen(
    uiState: StoreUiState,
    onSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    reserve: () -> Unit = {},
    quit: () -> Unit = {},
    changeSeat: () -> Unit = {},
    navigateToSignIn: () -> Unit = {},
) {

    when (uiState) {
        StoreUiState.Loading -> LoadingScreen(modifier = modifier)
        StoreUiState.NotFound -> EmptyResultScreen(
            subject = UiText.StringResource(R.string.store),
            modifier = modifier
        ) // TODO: Not Found
        is StoreUiState.Success -> {
            var buttonText: UiText = UiText.DynamicString("Not resolved")
            var enabled = false
            var onClick = {}
            // not signed in
            if (uiState.userStateAndUsedSeatPosition.userState == null) {
                buttonText = UiText.StringResource(R.string.sign_in_before_reservation)
                enabled = true
                onClick = navigateToSignIn
            } else { // signed in
                if (uiState.userStateAndUsedSeatPosition.seatPosition == null) { // not in session
                    buttonText = UiText.StringResource(R.string.reserve_seat)
                    onClick = reserve
                    enabled = uiState.selectedSeat != null
                } else { // in session
                    enabled = true
                    if (uiState.selectedUsedSeat == false) { // selected different seat
                        buttonText = UiText.StringResource(R.string.quit_and_reserve)
                        onClick = changeSeat
                    } else { // not selected or selected same seat used
                        buttonText = UiText.StringResource(R.string.cancel_reservation)
                        onClick = quit
                    }
                }
            }


            SectionWithSeatsScreen(
                store = uiState.store,
                sectionsWithSeats = uiState.sectionWithSeats,
                onBackClick = onBackClick,
                isLoading = uiState.isLoading,
                buttonText = buttonText,
                onButtonClick = onClick,
                buttonEnabled = !uiState.isLoading && enabled,
                selectedSeat = uiState.selectedSeat,
                modifier = modifier,
                userMessage = uiState.userMessage,
                onSelect = onSelect
            )
        }

        is StoreUiState.Error -> ErrorScreen(exception = uiState.exception, modifier = modifier)
    }
}

@Composable
private fun SectionWithSeatsScreen(
    store: Store,
    sectionsWithSeats: List<SectionWithSeats>,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    buttonText: UiText,
    onButtonClick: () -> Unit,
    buttonEnabled: Boolean,
    onSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    userMessage: Message? = null,
    selectedSeat: SelectedSeat? = null,
) {
    ScreenWithTopAppBar(
        title = UiText.DynamicString(store.name),
        showNavigateUp = true,
        onBackClick = onBackClick,
        modifier = modifier.fillMaxSize()
    ) { scaffoldPadding ->
        ComponentWithBottomButtons(
            bottomButtons = {
                BottomButton(
                    text = buttonText,
                    isLoading = isLoading,
                    onClick = onButtonClick,
                    enabled = buttonEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            showGradientBackground = true,
            modifier = Modifier
                .padding(scaffoldPadding)
                .imePadding()
                .fillMaxSize(),
            optionalContentOfButtonTop = {
                if (userMessage != null) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = userMessage.title.asString(),
                                color = userMessage.severity.textColor(),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )

                            userMessage.action.forEach {
                                TextButton(onClick = it.action) {
                                    Text(
                                        text = it.title.asString(),
                                        textDecoration = TextDecoration.Underline
                                    )
                                }
                            }
                        }
                        userMessage.content?.asString()?.let {
                            Text(
                                text = it,
                                textAlign = TextAlign.Start,
                            )
                        }
                    }
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(text = store.toString())
                }
                sectionsWithSeats.forEach { sectionWithSeats ->
                    item {
                        Text(text = sectionWithSeats.section.toString())
                    }
                    items(sectionWithSeats.seats, key = { it.id }) { seat ->
                        val isSelected =
                            selectedSeat?.seatId == seat.id && selectedSeat.sectionId == sectionWithSeats.section.id
                        ListItem(
                            modifier = Modifier
                                .selectable(
                                    selected = isSelected,
                                    onClick = { onSelect(sectionWithSeats.section.id, seat.id) }
                                )
                                .fillMaxWidth(),
                            leadingContent = {
                                if (isSelected) {
                                    UiIcon.ImageVectorIcon(CafeIcons.Checked).IconComposable()
                                }
                            },
                            headlineContent = {
                                Text(text = seat.name)
                            },
                            supportingContent = {
                                Text(text = seat.id)
                            },
                            trailingContent = {
                                Text(text = seat.isAvailable.toString())
                            },
                            tonalElevation = if (isSelected) 4.dp else 0.dp
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(200.dp))
                    }
                }
            }
        }
    }
}