package com.tugi64.tugisline.data.model

import androidx.compose.ui.graphics.Color

data class Layer(
    val id: String,
    val name: String,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val isFrozen: Boolean = false,
    val color: Color = Color.White,
    val lineType: LineType = LineType.CONTINUOUS,
    val lineWeight: Float = 1f,
    val canPlot: Boolean = true,
    val transparency: Float = 0f
)

enum class LineType {
    CONTINUOUS,
    DASHED,
    DOTTED,
    DASHDOT,
    CENTER
}

data class Entity(
    val id: String,
    val layerId: String,
    val type: EntityType,
    val points: List<Point3D>,
    val color: Color,
    val lineWeight: Float,
    val rotation: Float = 0f,
    val scale: Float = 1f
)

enum class EntityType {
    LINE,
    POLYLINE,
    CIRCLE,
    ARC,
    RECTANGLE,
    TEXT,
    DIMENSION
}

data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float = 0f
)

