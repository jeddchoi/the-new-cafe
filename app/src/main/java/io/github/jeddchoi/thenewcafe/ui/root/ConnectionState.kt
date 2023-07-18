package io.github.jeddchoi.thenewcafe.ui.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.jeddchoi.data.util.ConnectedState
import io.github.jeddchoi.thenewcafe.R

@Composable
fun ConnectionState(connectedState: ConnectedState) {
    when (connectedState) {
        ConnectedState.CONNECTED -> {}
        ConnectedState.FOUND_CONNECTION -> {
            Surface(
                color = colorResource(id = R.color.positive_background),
            ) {
                Text(
                    text = stringResource(R.string.connected_again),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        ConnectedState.LOST -> {
            Text(
                text = stringResource(R.string.not_connected),
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
            )
        }
    }
}