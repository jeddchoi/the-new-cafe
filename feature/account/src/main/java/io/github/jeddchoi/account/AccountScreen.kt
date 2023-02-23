package io.github.jeddchoi.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.LogCompositions
import io.github.jeddchoi.ui.feature.UiState


@Composable
fun AccountRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onShowActionLog: () -> Unit = {},
) {
    LogCompositions(tag = "TAG", msg = "AccountRoute")
    val viewModel: AccountViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(UiState.Loading())

    AccountScreen(uiState, onShowActionLog = onShowActionLog)
}


@Composable
fun AccountScreen(
    uiState: UiState<AccountUiStateData>,
    modifier: Modifier = Modifier,
    onShowActionLog: () -> Unit = {},
) {
    LogCompositions(tag = "TAG", msg = "AccountScreen")
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = when (uiState) {
            UiState.Empty -> "EMPTY"
            is UiState.Error -> "ERROR : ${uiState.exception.message}"
            is UiState.Loading -> "LOADING ${uiState.data?.data}"
            is UiState.Success -> "SUCCESS 🎉 ${uiState.data.data}"
        }
        Text(
            text = text,
            textAlign = TextAlign.Center
        )

        Button(onClick = onShowActionLog) {
            Text(text = stringResource(id = R.string.show_action_log))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    TheNewCafeTheme {
//        AccountScreen(
////            UiState.Success(AccountUiStateData("Hello!")),
//        )
    }
}