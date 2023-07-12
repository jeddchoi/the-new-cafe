package io.github.jeddchoi.ui.fullscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

@Composable
fun NotAuthenticatedScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        AuthenticationLottie(modifier = Modifier.size(250.dp))
        Text(
            text = stringResource(id = R.string.not_authenticated)
        )
        BottomButton(
            text = UiText.StringResource(R.string.sign_in),
            isLoading = false,
            onClick = navigateToSignIn,
            enabled = true,
        )
    }
}