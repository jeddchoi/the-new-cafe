package io.github.jeddchoi.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen
import io.github.jeddchoi.ui.fullscreen.NotAuthenticatedScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    navigateToSignIn: () -> Unit = {},
    signOut: () -> Unit = {},
) {
    when (uiState) {
        ProfileUiState.InitialLoading -> {
            LoadingScreen(modifier)
        }

        ProfileUiState.NotAuthenticated -> {
            NotAuthenticatedScreen(
                modifier = modifier,
                navigateToSignIn = {
                    signOut()
                }
            )
        }

        is ProfileUiState.Success -> {
            ScreenWithTopAppBar(
                title = UiText.StringResource(R.string.profile),
            ) {
                Column(
                    modifier = modifier.padding(it).fillMaxSize(),
                ) {
                    Text(
                        text = uiState.profile.toString()
                    )
                    Text(
                        text = uiState.feedback.toString()
                    )
                    Button(
                        onClick = signOut
                    ) {
                        Text(
                            text = stringResource(R.string.sign_out)
                        )
                    }
                }
            }
        }

        is ProfileUiState.Error -> {
            ErrorScreen(exception = uiState.exception, modifier = modifier)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    TheNewCafeTheme {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ProfileScreen(
            uiState = uiState)
    }
}