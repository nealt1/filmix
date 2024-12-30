package org.filmix.app.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * https://www.composables.com/icons
 */
object MaterialIcons {
    val Movie by lazy {
        ImageVector.Builder(
            name = "movie",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6.292f, 6.917f)
                lineToRelative(3.083f, 6.291f)
                horizontalLineToRelative(5.208f)
                lineToRelative(-3.041f, -6.291f)
                horizontalLineToRelative(3.541f)
                lineToRelative(3.084f, 6.291f)
                horizontalLineToRelative(5.208f)
                lineToRelative(-3.083f, -6.291f)
                horizontalLineToRelative(3.583f)
                lineToRelative(3.083f, 6.291f)
                horizontalLineToRelative(5.209f)
                lineToRelative(-3.084f, -6.291f)
                horizontalLineToRelative(4.667f)
                quadToRelative(1.083f, 0f, 1.854f, 0.791f)
                quadToRelative(0.771f, 0.792f, 0.771f, 1.834f)
                verticalLineToRelative(20.916f)
                quadToRelative(0f, 1.042f, -0.771f, 1.834f)
                quadToRelative(-0.771f, 0.791f, -1.854f, 0.791f)
                horizontalLineTo(6.25f)
                quadToRelative(-1.083f, 0f, -1.854f, -0.791f)
                quadToRelative(-0.771f, -0.792f, -0.771f, -1.834f)
                verticalLineTo(9.542f)
                quadToRelative(0f, -1.084f, 0.792f, -1.854f)
                quadToRelative(0.791f, -0.771f, 1.875f, -0.771f)
                close()
                moveToRelative(-0.042f, 8.916f)
                verticalLineToRelative(14.625f)
                horizontalLineToRelative(27.5f)
                verticalLineTo(15.833f)
                close()
                moveToRelative(0f, 0f)
                verticalLineToRelative(14.625f)
                close()
            }
        }.build()
    }

    val Translate by lazy {
        ImageVector.Builder(
            name = "translate",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22.125f, 36.375f)
                quadToRelative(-0.917f, 0f, -1.354f, -0.604f)
                quadToRelative(-0.438f, -0.604f, -0.104f, -1.521f)
                lineToRelative(6f, -15.958f)
                quadToRelative(0.25f, -0.667f, 0.895f, -1.125f)
                quadToRelative(0.646f, -0.459f, 1.396f, -0.459f)
                quadToRelative(0.667f, 0f, 1.354f, 0.459f)
                quadToRelative(0.688f, 0.458f, 0.938f, 1.125f)
                lineToRelative(6.125f, 15.875f)
                quadToRelative(0.333f, 0.916f, -0.125f, 1.562f)
                reflectiveQuadToRelative(-1.458f, 0.646f)
                quadToRelative(-0.459f, 0f, -0.875f, -0.292f)
                quadToRelative(-0.417f, -0.291f, -0.542f, -0.708f)
                lineTo(33f, 31.292f)
                horizontalLineToRelative(-7.917f)
                lineToRelative(-1.5f, 4.083f)
                quadToRelative(-0.166f, 0.417f, -0.583f, 0.708f)
                quadToRelative(-0.417f, 0.292f, -0.875f, 0.292f)
                close()
                moveToRelative(3.917f, -8f)
                horizontalLineToRelative(5.833f)
                lineToRelative(-2.875f, -8f)
                horizontalLineToRelative(-0.125f)
                close()
                moveTo(12.167f, 14.333f)
                quadToRelative(0.583f, 1.084f, 1.333f, 2.105f)
                quadToRelative(0.75f, 1.02f, 1.625f, 2.062f)
                quadToRelative(1.833f, -1.917f, 3.063f, -3.979f)
                quadToRelative(1.229f, -2.063f, 2.062f, -4.354f)
                horizontalLineTo(3.625f)
                quadToRelative(-0.667f, 0f, -1.146f, -0.479f)
                quadTo(2f, 9.208f, 2f, 8.542f)
                quadToRelative(0f, -0.709f, 0.479f, -1.188f)
                reflectiveQuadToRelative(1.146f, -0.479f)
                horizontalLineToRelative(9.833f)
                verticalLineTo(5.25f)
                quadToRelative(0f, -0.708f, 0.48f, -1.167f)
                quadToRelative(0.479f, -0.458f, 1.187f, -0.458f)
                quadToRelative(0.667f, 0f, 1.146f, 0.458f)
                quadToRelative(0.479f, 0.459f, 0.479f, 1.167f)
                verticalLineToRelative(1.625f)
                horizontalLineToRelative(9.833f)
                quadToRelative(0.709f, 0f, 1.167f, 0.479f)
                quadToRelative(0.458f, 0.479f, 0.458f, 1.188f)
                quadToRelative(0f, 0.666f, -0.458f, 1.146f)
                quadToRelative(-0.458f, 0.479f, -1.167f, 0.479f)
                horizontalLineToRelative(-3.041f)
                quadToRelative(-0.875f, 2.833f, -2.375f, 5.562f)
                reflectiveQuadTo(17.5f, 20.917f)
                lineToRelative(4f, 4.041f)
                lineToRelative(-1.25f, 3.334f)
                lineToRelative(-5.125f, -5f)
                lineToRelative(-7.083f, 7.041f)
                quadToRelative(-0.459f, 0.5f, -1.125f, 0.5f)
                quadToRelative(-0.667f, 0f, -1.125f, -0.5f)
                quadToRelative(-0.5f, -0.458f, -0.5f, -1.125f)
                quadToRelative(0f, -0.666f, 0.5f, -1.166f)
                lineToRelative(7.166f, -7.167f)
                quadToRelative(-1.083f, -1.292f, -2.02f, -2.583f)
                quadTo(10f, 17f, 9.25f, 15.625f)
                quadToRelative(-0.5f, -0.875f, -0.083f, -1.521f)
                quadToRelative(0.416f, -0.646f, 1.5f, -0.646f)
                quadToRelative(0.416f, 0f, 0.854f, 0.25f)
                quadToRelative(0.437f, 0.25f, 0.646f, 0.625f)
                close()
            }
        }.build()
    }
    val Download by lazy {
        ImageVector.Builder(
            name = "download",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 26.25f)
                quadToRelative(-0.25f, 0f, -0.479f, -0.083f)
                quadToRelative(-0.229f, -0.084f, -0.438f, -0.292f)
                lineToRelative(-6.041f, -6.083f)
                quadToRelative(-0.417f, -0.375f, -0.396f, -0.917f)
                quadToRelative(0.021f, -0.542f, 0.396f, -0.917f)
                reflectiveQuadToRelative(0.916f, -0.396f)
                quadToRelative(0.542f, -0.02f, 0.959f, 0.396f)
                lineToRelative(3.791f, 3.792f)
                verticalLineTo(8.292f)
                quadToRelative(0f, -0.584f, 0.375f, -0.959f)
                reflectiveQuadTo(20f, 6.958f)
                quadToRelative(0.542f, 0f, 0.938f, 0.375f)
                quadToRelative(0.395f, 0.375f, 0.395f, 0.959f)
                verticalLineTo(21.75f)
                lineToRelative(3.792f, -3.792f)
                quadToRelative(0.375f, -0.416f, 0.917f, -0.396f)
                quadToRelative(0.541f, 0.021f, 0.958f, 0.396f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
                reflectiveQuadToRelative(-0.375f, 0.958f)
                lineToRelative(-6.083f, 6.042f)
                quadToRelative(-0.209f, 0.208f, -0.438f, 0.292f)
                quadToRelative(-0.229f, 0.083f, -0.479f, 0.083f)
                close()
                moveTo(9.542f, 32.958f)
                quadToRelative(-1.042f, 0f, -1.834f, -0.791f)
                quadToRelative(-0.791f, -0.792f, -0.791f, -1.834f)
                verticalLineToRelative(-4.291f)
                quadToRelative(0f, -0.542f, 0.395f, -0.938f)
                quadToRelative(0.396f, -0.396f, 0.938f, -0.396f)
                quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.938f)
                verticalLineToRelative(4.291f)
                horizontalLineToRelative(20.916f)
                verticalLineToRelative(-4.291f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                quadToRelative(0.583f, 0f, 0.958f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.938f)
                verticalLineToRelative(4.291f)
                quadToRelative(0f, 1.042f, -0.791f, 1.834f)
                quadToRelative(-0.792f, 0.791f, -1.834f, 0.791f)
                close()
            }
        }.build()
    }
}