package com.tugi64.tugisline.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class MeasurementResult(
    val type: MeasurementType,
    val value: String,
    val unit: String,
    val startPoint: Point3D? = null,
    val endPoint: Point3D? = null,
    val points: List<Point3D> = emptyList()
)

enum class MeasurementType {
    LINEAR,
    RADIAL,
    ANGLE,
    AREA
}

data class ViewState(
    val zoom: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f
)

data class CoordinateInfo(
    val x: Float,
    val y: Float,
    val z: Float,
    val isAbsolute: Boolean = true
)

data class DrawnShape(
    val type: ShapeType,
    val startPoint: Offset,
    val endPoint: Offset? = null,
    val color: Color = Color.White,
    val strokeWidth: Float = 2f
)

enum class ShapeType {
    LINE,
    RECTANGLE,
    CIRCLE,
    ARC,
    TEXT
}

