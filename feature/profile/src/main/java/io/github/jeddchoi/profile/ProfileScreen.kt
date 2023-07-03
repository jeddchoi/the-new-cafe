package io.github.jeddchoi.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.CircularProfilePicture
import io.github.jeddchoi.ui.feature.EmptyResultScreen
import io.github.jeddchoi.ui.feature.ErrorScreen
import io.github.jeddchoi.ui.feature.LoadingScreen
import io.github.jeddchoi.ui.model.UiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    uiState: UiState<ProfileUiStateData>,
    onNavigateToSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            MediumTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = stringResource(id = R.string.profile))
                },
            )
        }

        when (uiState) {
            UiState.EmptyResult -> {
                item {
                    EmptyResultScreen(subject = stringResource(id = R.string.profile))
                }
            }

            is UiState.Error -> {
                item {
                    ErrorScreen(exception = uiState.exception, modifier = modifier)
                }
            }

            is UiState.InitialLoading -> {
                item {
                    LoadingScreen(modifier)
                }
            }

            is UiState.Success -> {
                item {
                    CircularProfilePicture(image = painterResource(id = R.drawable.sample_avatar))
                }

                item {
                    Text(
                        text = uiState.data.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Button(onClick = onNavigateToSignIn) {
                        Text(text = stringResource(id = R.string.navigate_to_sign_in))
                    }
                }
                item {
                    Button(onClick = onSignOut) {
                        Text(text = stringResource(id = R.string.sign_out))
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    TheNewCafeTheme {
//        AccountScreen(
////            UiState.Success(ProfileUiStateData("Hello!")),
//        )
    }
}