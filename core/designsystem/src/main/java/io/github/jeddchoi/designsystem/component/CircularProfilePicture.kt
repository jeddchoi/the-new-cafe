package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.designsystem.R

@Composable
fun CircularProfilePicture(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = stringResource(id = R.string.profile_picture_desc),
        contentScale = ContentScale.Crop,            // crop the image if it's not a square
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)                       // clip to the circle shape
    )
}
