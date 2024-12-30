package org.filmix.app.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

class ShadeIndication(private val showShade: State<Boolean>) : Indication {
    private class ShadeIndicationInstance(
        private val showShade: State<Boolean>,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (!showShade.value) {
                drawRect(color = Color.Black.copy(alpha = 0.2f), size = size)
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return remember(interactionSource) {
            ShadeIndicationInstance(showShade)
        }
    }
}