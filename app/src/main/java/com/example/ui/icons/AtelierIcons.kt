package com.example.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Atelier Icons - Proprietary SVG Icon Set for Atelier v3.0
 * Custom vector geometry crafted strictly for the Atelier design system.
 */
object AtelierIcons {

    val Chat: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Chat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 11.5f)
                curveTo(21f, 16.194f, 16.97f, 20f, 12f, 20f)
                curveTo(10.3f, 20f, 8.7f, 19.5f, 7.35f, 18.65f)
                lineTo(3f, 20f)
                lineTo(4.45f, 15.85f)
                curveTo(3.55f, 14.55f, 3f, 13.08f, 3f, 11.5f)
                curveTo(3f, 6.806f, 7.03f, 3f, 12f, 3f)
                curveTo(16.97f, 3f, 21f, 6.806f, 21f, 11.5f)
                close()
            }
        }.build()
    }

    val Batch: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Batch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Stack layers representing batch processing
                moveTo(4f, 7f)
                lineTo(12f, 3f)
                lineTo(20f, 7f)
                lineTo(12f, 11f)
                close()

                moveTo(4f, 12f)
                lineTo(12f, 16f)
                lineTo(20f, 12f)

                moveTo(4f, 17f)
                lineTo(12f, 21f)
                lineTo(20f, 17f)
            }
        }.build()
    }

    val Library: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Library",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Grid/Gallery collection icon
                moveTo(3f, 3f)
                lineTo(10f, 3f)
                lineTo(10f, 10f)
                lineTo(3f, 10f)
                close()

                moveTo(14f, 3f)
                lineTo(21f, 3f)
                lineTo(21f, 10f)
                lineTo(14f, 10f)
                close()

                moveTo(3f, 14f)
                lineTo(10f, 14f)
                lineTo(10f, 21f)
                lineTo(3f, 21f)
                close()

                moveTo(14f, 14f)
                lineTo(21f, 14f)
                lineTo(21f, 21f)
                lineTo(14f, 21f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Precision slider/gears icon
                moveTo(4f, 6f)
                lineTo(20f, 6f)
                moveTo(4f, 12f)
                lineTo(20f, 12f)
                moveTo(4f, 18f)
                lineTo(20f, 18f)

                moveTo(8f, 3f)
                lineTo(8f, 9f)

                moveTo(16f, 9f)
                lineTo(16f, 15f)

                moveTo(10f, 15f)
                lineTo(10f, 21f)
            }
        }.build()
    }

    val Sparkles: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Sparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2f)
                curveTo(12f, 7f, 14f, 9f, 19f, 10f)
                curveTo(14f, 11f, 12f, 13f, 12f, 18f)
                curveTo(12f, 13f, 10f, 11f, 5f, 10f)
                curveTo(10f, 9f, 12f, 7f, 12f, 2f)
                close()

                moveTo(19f, 16f)
                lineTo(19f, 20f)
                moveTo(17f, 18f)
                lineTo(21f, 18f)
            }
        }.build()
    }

    val Send: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Send",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 2f)
                lineTo(11f, 13f)
                moveTo(22f, 2f)
                lineTo(15f, 22f)
                lineTo(11f, 13f)
                lineTo(2f, 9f)
                close()
            }
        }.build()
    }

    val Stop: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Stop",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(6f, 6f)
                lineTo(18f, 6f)
                lineTo(18f, 18f)
                lineTo(6f, 18f)
                close()
            }
        }.build()
    }

    val Image: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 5f)
                curveTo(3f, 3.89f, 3.89f, 3f, 5f, 3f)
                lineTo(19f, 3f)
                curveTo(20.11f, 3f, 21f, 3.89f, 21f, 5f)
                lineTo(21f, 19f)
                curveTo(21f, 20.11f, 20.11f, 21f, 19f, 21f)
                lineTo(5f, 21f)
                curveTo(3.89f, 21f, 3f, 20.11f, 3f, 19f)
                close()

                moveTo(8.5f, 8.5f)
                curveTo(8.5f, 7.67f, 9.17f, 7f, 10f, 7f)
                curveTo(10.83f, 7f, 11.5f, 7.67f, 11.5f, 8.5f)
                curveTo(11.5f, 9.33f, 10.83f, 10f, 10f, 10f)
                curveTo(9.17f, 10f, 8.5f, 9.33f, 8.5f, 8.5f)
                close()

                moveTo(21f, 15f)
                lineTo(16f, 10f)
                lineTo(5f, 21f)
            }
        }.build()
    }

    val Video: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Video",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 6f)
                curveTo(3f, 4.9f, 3.9f, 4f, 5f, 4f)
                lineTo(15f, 4f)
                curveTo(16.1f, 4f, 17f, 4.9f, 17f, 6f)
                lineTo(17f, 18f)
                curveTo(17f, 19.1f, 16.1f, 20f, 15f, 20f)
                lineTo(5f, 20f)
                curveTo(3.9f, 20f, 3f, 19.1f, 3f, 18f)
                close()

                moveTo(17f, 9f)
                lineTo(22f, 5.5f)
                lineTo(22f, 18.5f)
                lineTo(17f, 15f)
            }
        }.build()
    }

    val Key: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Key",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 2f)
                lineTo(10.5f, 12.5f)
                moveTo(10.5f, 12.5f)
                curveTo(8.5f, 10.5f, 5f, 10.5f, 3f, 12.5f)
                curveTo(1f, 14.5f, 1f, 18f, 3f, 20f)
                curveTo(5f, 22f, 8.5f, 22f, 10.5f, 20f)
                curveTo(12.5f, 18f, 12.5f, 14.5f, 10.5f, 12.5f)
                close()

                moveTo(16f, 7f)
                lineTo(19f, 10f)

                moveTo(13.5f, 9.5f)
                lineTo(15.5f, 11.5f)
            }
        }.build()
    }

    val Play: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Play",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(6f, 4f)
                lineTo(20f, 12f)
                lineTo(6f, 20f)
                close()
            }
        }.build()
    }

    val Pause: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(6f, 4f)
                lineTo(10f, 4f)
                lineTo(10f, 20f)
                lineTo(6f, 20f)
                close()

                moveTo(14f, 4f)
                lineTo(18f, 4f)
                lineTo(18f, 20f)
                lineTo(14f, 20f)
                close()
            }
        }.build()
    }

    val Refresh: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Refresh",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(23f, 4f)
                lineTo(23f, 10f)
                lineTo(17f, 10f)

                moveTo(1f, 20f)
                lineTo(1f, 14f)
                lineTo(7f, 14f)

                moveTo(3.51f, 9f)
                curveTo(4.73f, 5.99f, 8.12f, 4f, 12f, 4f)
                curveTo(16.74f, 4f, 20.67f, 7.37f, 21.68f, 11.83f)

                moveTo(20.49f, 15f)
                curveTo(19.27f, 18.01f, 15.88f, 20f, 12f, 20f)
                curveTo(7.26f, 20f, 3.33f, 16.63f, 2.32f, 12.17f)
            }
        }.build()
    }

    val Copy: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Copy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 4f)
                lineTo(19f, 4f)
                curveTo(20.1f, 4f, 21f, 4.9f, 21f, 6f)
                lineTo(21f, 17f)
                moveTo(4f, 8f)
                lineTo(15f, 8f)
                curveTo(16.1f, 8f, 17f, 8.9f, 17f, 10f)
                lineTo(17f, 21f)
                curveTo(17f, 22.1f, 16.1f, 23f, 15f, 23f)
                lineTo(4f, 23f)
                curveTo(2.9f, 23f, 2f, 22.1f, 2f, 21f)
                lineTo(2f, 10f)
                curveTo(2f, 8.9f, 2.9f, 8f, 4f, 8f)
                close()
            }
        }.build()
    }

    val Download: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Download",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 3f)
                lineTo(12f, 15f)
                moveTo(7f, 10f)
                lineTo(12f, 15f)
                lineTo(17f, 10f)
                moveTo(4f, 19f)
                lineTo(20f, 19f)
            }
        }.build()
    }

    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 5f)
                lineTo(12f, 19f)
                moveTo(5f, 12f)
                lineTo(19f, 12f)
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Check",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 12f)
                lineTo(9f, 17f)
                lineTo(20f, 6f)
            }
        }.build()
    }

    val Delete: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 6f)
                lineTo(21f, 6f)
                moveTo(8f, 6f)
                lineTo(8f, 3f)
                lineTo(16f, 3f)
                lineTo(16f, 6f)
                moveTo(19f, 6f)
                lineTo(18f, 20f)
                lineTo(6f, 20f)
                lineTo(5f, 6f)
            }
        }.build()
    }

    val Variation: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Variation",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 3f)
                lineTo(21f, 8f)
                lineTo(16f, 13f)
                moveTo(21f, 8f)
                lineTo(9f, 8f)
                curveTo(5.5f, 8f, 3f, 10.5f, 3f, 14f)
                curveTo(3f, 17.5f, 5.5f, 20f, 9f, 20f)
                lineTo(15f, 20f)
            }
        }.build()
    }

    val User: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.User",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 11f)
                curveTo(14.21f, 11f, 16f, 9.21f, 16f, 7f)
                curveTo(16f, 4.79f, 14.21f, 3f, 12f, 3f)
                curveTo(9.79f, 3f, 8f, 4.79f, 8f, 7f)
                curveTo(8f, 9.21f, 9.79f, 11f, 12f, 11f)
                close()

                moveTo(4f, 20f)
                curveTo(4f, 16.5f, 7.5f, 14f, 12f, 14f)
                curveTo(16.5f, 14f, 20f, 16.5f, 20f, 20f)
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "Atelier.Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11f, 19f)
                curveTo(15.42f, 19f, 19f, 15.42f, 19f, 11f)
                curveTo(19f, 6.58f, 15.42f, 3f, 11f, 3f)
                curveTo(6.58f, 3f, 3f, 6.58f, 3f, 11f)
                curveTo(3f, 15.42f, 6.58f, 19f, 11f, 19f)
                close()

                moveTo(21f, 21f)
                lineTo(16.65f, 16.65f)
            }
        }.build()
    }
}
