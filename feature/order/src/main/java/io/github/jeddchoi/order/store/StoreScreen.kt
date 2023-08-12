package io.github.jeddchoi.order.store

import android.os.Build
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.textColor
import io.github.jeddchoi.model.Store
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import io.github.jeddchoi.order.R
import io.github.jeddchoi.ui.component.ComponentWithBottomButtons
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.fullscreen.EmptyResultScreen
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen

@OptIn(ExperimentalPermissionsApi::class)
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
    setUserMessage: (Message?) -> Unit = {},
) {
//    val bluetoothManager = getSystemService(LocalContext.current, BluetoothManager::class.java)
//    bluetoothManager?.adapter

    val servicePermissionState = rememberMultiplePermissionsState(
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(android.Manifest.permission.BLUETOOTH_SCAN)
                add(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    )

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
            if (uiState.userStateAndUsedSeatPosition == null) {
                buttonText = UiText.StringResource(R.string.sign_in_before_reservation)
                enabled = true
                onClick = navigateToSignIn
            } else { // signed in
                if (uiState.userStateAndUsedSeatPosition == UserStateAndUsedSeatPosition.None) { // not in session
                    buttonText = UiText.StringResource(R.string.reserve_seat)
                    onClick = {

                        if (servicePermissionState.allPermissionsGranted.not()) {
                            servicePermissionState.launchMultiplePermissionRequest()
                        } else {
                            reserve()
                        }
                    }
                    enabled = uiState.selectedSeat != null
                } else { // in session
                    enabled = true
                    if (uiState.selectedUsedSeat == false) { // selected different seat
                        buttonText = UiText.StringResource(R.string.quit_and_reserve)
                        onClick = {
                            if (servicePermissionState.allPermissionsGranted.not()) {
                                servicePermissionState.launchMultiplePermissionRequest()
                            } else {
                                changeSeat()
                            }
                        }
                    } else { // not selected or selected same seat used
                        buttonText = UiText.StringResource(R.string.cancel_reservation)
                        onClick = quit
                    }
                }
            }


            SectionWithSeatsScreen(
                store = uiState.store,
                sectionsWithSeats = uiState.sectionWithSeats,
                modifier = modifier,
                isLoading = uiState.isLoading,
                userMessage = uiState.userMessage,
                buttonText = buttonText,
                buttonEnabled = !uiState.isLoading && enabled,
                selectedSeat = uiState.selectedSeat,
                onSelect = onSelect,
                onBackClick = onBackClick,
                onButtonClick = onClick
            )

            LaunchedEffect(
                servicePermissionState.allPermissionsGranted,
                servicePermissionState.shouldShowRationale,
                uiState.userStateAndUsedSeatPosition,
                uiState.selectedSeat,
            ) {
                if (servicePermissionState.allPermissionsGranted.not()) {
                    if (servicePermissionState.shouldShowRationale) {
                        setUserMessage(
                            Message(
                                title = UiText.StringResource(R.string.error),
                                content = UiText.StringResource(R.string.permissions_rationale),
                                severity = Message.Severity.ERROR
                            )
                        )
                    } else if (uiState.userStateAndUsedSeatPosition is UserStateAndUsedSeatPosition.UsingSeat && uiState.selectedSeat != null) {
                        setUserMessage(
                            Message(
                                title = UiText.StringResource(R.string.warning),
                                content = UiText.StringResource(R.string.permissions_required,
                                    servicePermissionState.permissions.joinToString("\n") {
                                        it.permission
                                    }),
                                severity = Message.Severity.WARNING
                            )
                        )
                    } else {
                        setUserMessage(null)
                    }
                }
            }
        }

        is StoreUiState.Error -> ErrorScreen(exception = uiState.exception, modifier = modifier)
    }
}

@Composable
private fun SectionWithSeatsScreen(
    store: Store,
    sectionsWithSeats: List<SectionWithSeats>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    userMessage: Message? = null,
    buttonText: UiText = UiText.PlaceHolder,
    buttonEnabled: Boolean = false,
    selectedSeat: SelectedSeat? = null,
    onSelect: (String, String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    onButtonClick: () -> Unit = {},
) {
    ScreenWithTopAppBar(
        title = UiText.DynamicString(store.name),
        showNavigateUp = true,
        clickBack = onBackClick,
        modifier = modifier.fillMaxSize()
    ) { scaffoldPadding ->
        ComponentWithBottomButtons(
            modifier = Modifier
                .padding(scaffoldPadding)
                .imePadding()
                .fillMaxSize(),
            bottomButtons = {
                BottomButton(
                    text = buttonText,
                    isLoading = isLoading,
                    click = onButtonClick,
                    enabled = buttonEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            showGradientBackground = true,
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
                    .align(Alignment.TopCenter)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(text = store.toString())
                }
                sectionsWithSeats.sortedBy { it.section.name }.forEach { sectionWithSeats ->
                    item {
                        Text(text = sectionWithSeats.section.toString())
                    }
                    items(sectionWithSeats.seats.sortedBy { it.name }, key = { it.id }) { seat ->
                        val isSelected =
                            selectedSeat?.seatId == seat.id && selectedSeat.sectionId == sectionWithSeats.section.id
                        ListItem(
                            modifier = Modifier
                                .selectable(
                                    selected = isSelected,
                                    enabled = seat.isAvailable,
                                    onClick = { onSelect(sectionWithSeats.section.id, seat.id) }
                                )
                                .fillMaxWidth(),
                            leadingContent = {
                                if (isSelected) {
                                    UiIcon.ImageVectorIcon(CafeIcons.CheckCircle).ToComposable()
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