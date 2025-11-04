package com.tugi64.tugisline.data.model

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

