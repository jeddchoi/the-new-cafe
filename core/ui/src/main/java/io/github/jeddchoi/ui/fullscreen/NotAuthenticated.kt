package io.github.jeddchoi.ui.fullscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.component.lottie.AuthenticationLottie
import io.github.jeddchoi.ui.R
import io.github.jeddchoi.ui.component.ComponentWithBottomButtons

@Composable
fun NotAuthenticatedScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit = {},
) {
    ComponentWithBottomButtons(
        modifier = modifier,
        bottomButtons = {
            BottomButton(
                text = UiText.StringResource(R.string.sign_in),
                isLoading = false,
                click = navigateToSignIn,
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthenticationLottie(modifier = Modifier.size(250.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.not_authenticated)
            )
        }
    }
}