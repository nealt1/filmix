package org.filmix.app.ui.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun <T> DropdownPreference(
    title: String,
    items: Map<T, String>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dropDownExpanded by remember { mutableStateOf(value = false) }

    RegularPreference(
        title = title,
        subtitle = items.entries.first { it.key == selectedItem }.value,
        onClick = {
            dropDownExpanded = true
        },
        modifier = modifier
            .background(
                color = if (dropDownExpanded) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                } else {
                    Color.Unspecified
                }
            ),
        enabled = enabled,
    )

    Box {
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = !dropDownExpanded },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        dropDownExpanded = false
                        onItemSelected(item.key)
                    },
                    modifier = Modifier
                        .background(
                            color = if (selectedItem == item.key) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            } else {
                                Color.Unspecified
                            }
                        ),
                    text = {
                        Text(
                            text = item.value,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    enabled = selectedItem != item.key
                )
            }
        }
    }
}