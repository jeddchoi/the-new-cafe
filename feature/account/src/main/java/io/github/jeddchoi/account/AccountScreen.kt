package io.github.jeddchoi.account

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.feature.UiState


@Composable
internal fun AccountScreen(
    uiState: UiState<AccountUiStateData>,
    onNavigateToSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .scrollable(lazyListState, Orientation.Vertical),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = when (uiState) {
            UiState.EmptyResult -> "EMPTY"
            is UiState.Error -> "ERROR : ${uiState.exception.message}"
            is UiState.Loading -> "LOADING ${uiState.data?.data}"
            is UiState.Success -> "SUCCESS 🎉 ${uiState.data.data}"
        }
        Text(
            text = text,
            textAlign = TextAlign.Center
        )

        Button(onClick = onNavigateToSignIn) {
            Text(text = stringResource(id = R.string.navigate_to_sign_in))
        }
        Button(onClick = onSignOut) {
            Text(text = stringResource(id = R.string.sign_out))
        }
    }
}


@Preview(showBackground = true)
@Composable
internal fun AccountScreenPreview() {
    TheNewCafeTheme {
//        AccountScreen(
////            UiState.Success(AccountUiStateData("Hello!")),
//        )
    }
}