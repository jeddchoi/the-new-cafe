package io.github.jeddchoi.account

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.UiState

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun LogCompositions(tag: String, msg: String) {
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    Log.d(tag, "Compositions: $msg ${ref.value}")
}

@Composable
fun AccountRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,

    ) {
    LogCompositions(tag = "TAG", msg = "AccountRoute")
    val viewModel: AccountViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(UiState.Loading())

    AccountScreen(uiState)
}


@Composable
fun AccountScreen(
    uiState: UiState<AccountUiStateData>,
    modifier: Modifier = Modifier,
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
        LogCompositions(tag = "TAG", msg = "Column")
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
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