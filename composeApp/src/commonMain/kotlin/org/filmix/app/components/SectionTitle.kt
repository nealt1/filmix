package org.filmix.app.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.headlineSmall
    )
}