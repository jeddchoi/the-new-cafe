package io.github.jeddchoi.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.jeddchoi.common.OneShotFeedbackUiState
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.component.CircularProfilePicture
import io.github.jeddchoi.designsystem.component.item.BooleanItem
import io.github.jeddchoi.designsystem.component.item.DateTimeItem
import io.github.jeddchoi.designsystem.component.item.EnumItem
import io.github.jeddchoi.designsystem.component.item.StringItem
import io.github.jeddchoi.model.Sex
import io.github.jeddchoi.model.UserProfile
import io.github.jeddchoi.ui.component.ComponentWithBottomButtons
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen
import io.github.jeddchoi.ui.fullscreen.NotAuthenticatedScreen


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
                navigateToSignIn = navigateToSignIn
            )
        }

        is ProfileUiState.Success -> {
            ScreenWithTopAppBar(
                title = UiText.StringResource(R.string.hello_user, uiState.profile.displayName),
            ) {
                ProfileContent(
                    modifier = modifier
                        .padding(it)
                        .fillMaxSize(),
                    userProfile = uiState.profile,
                    feedback = uiState.feedback,
                    signOut = signOut
                )
            }
        }

        is ProfileUiState.Error -> {
            ErrorScreen(exception = uiState.exception, modifier = modifier)
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    userProfile: UserProfile = UserProfile(),
    feedback: OneShotFeedbackUiState = OneShotFeedbackUiState(),
    signOut: () -> Unit = {},
) {
    ComponentWithBottomButtons(
        modifier = modifier,
        bottomButtons = {
            BottomButton(
                modifier = Modifier.fillMaxWidth(),
                click = signOut,
                text = UiText.StringResource(R.string.sign_out)
            )
        }
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (userProfile.profilePhotoUrl == null) {
                CircularProfilePicture(
                    image = painterResource(id = R.drawable.sample_avatar),
                    modifier = Modifier.size(150.dp),
                )
            } else {
                AsyncImage(
                    model = userProfile.profilePhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(150.dp),
                )
            }
            val modifierWithFullWidth = Modifier.fillMaxWidth()
            StringItem(
                modifier = modifierWithFullWidth,
                title = UiText.StringResource(R.string.email_address),
                content = UiText.DynamicString(userProfile.emailAddress)
            )

            DateTimeItem(
                modifier = modifierWithFullWidth,
                date = userProfile.creationTime,
                title = UiText.StringResource(R.string.created_at)
            )

            DateTimeItem(
                modifier = modifierWithFullWidth,
                date = userProfile.lastSignInTime,
                title = UiText.StringResource(R.string.last_sign_in_at)
            )


            BooleanItem(
                modifier = modifierWithFullWidth,
                title = UiText.StringResource(R.string.is_anonymous),
                switchOn = userProfile.isAnonymous
            )
            BooleanItem(
                modifier = modifierWithFullWidth,
                title = UiText.StringResource(R.string.is_email_verified),
                switchOn = userProfile.isEmailVerified
            )
            BooleanItem(
                modifier = modifierWithFullWidth,
                title = UiText.StringResource(R.string.is_online),
                switchOn = userProfile.isOnline
            )

            EnumItem(
                modifier = modifierWithFullWidth,
                currentIdx = userProfile.sex?.ordinal,
                values = Sex.values().map { it.name },
                title = UiText.StringResource(R.string.sex)
            )

            Spacer(
                modifier = Modifier.height(200.dp)
            )
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
            uiState = uiState
        )
    }
}

@Preview
@Composable
fun ProfileContentPreview() {
    TheNewCafeTheme {
        ProfileContent(
        )
    }
}