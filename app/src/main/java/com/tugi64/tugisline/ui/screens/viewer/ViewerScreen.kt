package com.tugi64.tugisline.ui.screens.viewer

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tugi64.tugisline.data.model.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    fileId: String,
    onNavigateBack: () -> Unit,
    onNavigateToLayers: () -> Unit
) {
    var viewState by remember { mutableStateOf(ViewState()) }
    var selectedTool by remember { mutableStateOf(ViewerTool.PAN) }
    var showLeftPanel by remember { mutableStateOf(false) }
    var showRightPanel by remember { mutableStateOf(false) }
    var showBottomBar by remember { mutableStateOf(true) }
    var cursorPosition by remember { mutableStateOf(Offset.Zero) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showPrintDialog by remember { mutableStateOf(false) }
    var gridSnapEnabled by remember { mutableStateOf(true) }

    if (showSaveDialog) {
        SaveDialog(onDismiss = { showSaveDialog = false })
    }

    if (showShareDialog) {
        ShareDialog(onDismiss = { showShareDialog = false })
    }

    if (showPrintDialog) {
        PrintDialog(onDismiss = { showPrintDialog = false })
    }

    Scaffold(
        topBar = {
            ViewerTopBar(
                fileName = "Mimari_Plan_A1.dwg",
                zoomLevel = (viewState.zoom * 100).toInt(),
                onNavigateBack = onNavigateBack,
                onSave = { showSaveDialog = true },
                onShare = { showShareDialog = true },
                onPrint = { showPrintDialog = true },
                onFitToScreen = {
                    viewState = ViewState(zoom = 1f, offsetX = 0f, offsetY = 0f)
                },
                onShowLayers = { showRightPanel = !showRightPanel }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                ViewerBottomBar(
                    cursorPosition = cursorPosition,
                    scale = "1:100",
                    units = "mm",
                    gridSnap = gridSnapEnabled,
                    onToggleGridSnap = { gridSnapEnabled = !gridSnapEnabled }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Canvas - Çizim Alanı
            DrawingCanvas(
                viewState = viewState,
                selectedTool = selectedTool,
                onViewStateChange = { viewState = it },
                onCursorMove = { cursorPosition = it },
                modifier = Modifier.fillMaxSize()
            )

            // Sol Araç Paleti
            AnimatedVisibility(
                visible = showLeftPanel,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                LeftToolPalette(
                    selectedTool = selectedTool,
                    onToolSelected = { tool ->
                        selectedTool = tool
                        // Tool'a göre aksiyon yap
                        when (tool) {
                            ViewerTool.ZOOM_IN -> {
                                viewState = viewState.copy(
                                    zoom = (viewState.zoom * 1.2f).coerceAtMost(10f)
                                )
                            }
                            ViewerTool.ZOOM_OUT -> {
                                viewState = viewState.copy(
                                    zoom = (viewState.zoom / 1.2f).coerceAtLeast(0.1f)
                                )
                            }
                            else -> {
                                // Diğer tool'lar için sadece seçili durumu değiştir
                            }
                        }
                    }
                )
            }

            // Sağ Panel (Katmanlar & Properties)
            AnimatedVisibility(
                visible = showRightPanel,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                RightPropertiesPanel()
            }

            // Floating Action Button - Araç Paletini Aç/Kapat
            FloatingActionButton(
                onClick = { showLeftPanel = !showLeftPanel },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    if (showLeftPanel) Icons.Default.KeyboardArrowLeft else Icons.Default.Menu,
                    contentDescription = "Araçlar"
                )
            }

            // Zoom Kontrolleri
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        viewState = viewState.copy(
                            zoom = (viewState.zoom * 1.2f).coerceAtMost(10f)
                        )
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Yakınlaştır")
                }
                FloatingActionButton(
                    onClick = {
                        viewState = viewState.copy(
                            zoom = (viewState.zoom / 1.2f).coerceAtLeast(0.1f)
                        )
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.HorizontalRule, contentDescription = "Uzaklaştır")
                }
                FloatingActionButton(
                    onClick = { viewState = ViewState() },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sıfırla")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerTopBar(
    fileName: String,
    zoomLevel: Int,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit,
    onFitToScreen: () -> Unit,
    onShowLayers: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    fileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Zoom: $zoomLevel%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
        },
        actions = {
            IconButton(onClick = onFitToScreen) {
                Icon(Icons.Default.CenterFocusWeak, contentDescription = "Ekrana Sığdır")
            }
            IconButton(onClick = onShowLayers) {
                Icon(Icons.Default.FilterList, contentDescription = "Katmanlar")
            }
            IconButton(onClick = onSave) {
                Icon(Icons.Default.SaveAlt, contentDescription = "Kaydet")
            }
            IconButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "Paylaş")
            }
            IconButton(onClick = onPrint) {
                Icon(Icons.Default.Send, contentDescription = "Yazdır")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun DrawingCanvas(
    viewState: ViewState,
    selectedTool: ViewerTool,
    onViewStateChange: (ViewState) -> Unit,
    onCursorMove: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    var offset by remember { mutableStateOf(Offset(viewState.offsetX, viewState.offsetY)) }
    var zoom by remember { mutableStateOf(viewState.zoom) }
    var drawnShapes by remember { mutableStateOf<List<com.tugi64.tugisline.data.model.DrawnShape>>(emptyList()) }
    var currentDrawing by remember { mutableStateOf<com.tugi64.tugisline.data.model.DrawnShape?>(null) }
    var drawingStartPoint by remember { mutableStateOf<Offset?>(null) }

    // Zoom state'i ViewState'ten senkronize et
    LaunchedEffect(viewState.zoom) {
        zoom = viewState.zoom
    }

    Canvas(
        modifier = modifier
            .background(Color(0xFF1E1E1E))
            .pointerInput(Unit) {
                // Mouse scroll wheel için zoom - PointerEventType.Scroll kullanarak
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        // Scroll wheel event'ini yakala
                        event.changes.firstOrNull()?.let { change ->
                            val scrollDelta = change.scrollDelta

                            if (scrollDelta.y != 0f) {
                                // Scroll yukarı (pozitif): zoom in
                                // Scroll aşağı (negatif): zoom out
                                val zoomFactor = if (scrollDelta.y > 0) 0.9f else 1.1f
                                val newZoom = (zoom * zoomFactor).coerceIn(0.1f, 10f)
                                zoom = newZoom
                                onViewStateChange(ViewState(newZoom, offset.x, offset.y))
                                change.consume()
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                // İki parmak gesture desteği
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    offset += pan
                    val newZoom = (zoom * gestureZoom).coerceIn(0.1f, 10f)
                    zoom = newZoom
                    onViewStateChange(ViewState(newZoom, offset.x, offset.y))
                }
            }
            .pointerInput(Unit) {
                // Tek parmak drag desteği
                detectDragGestures(
                    onDragStart = { startOffset ->
                        when (selectedTool) {
                            ViewerTool.LINE, ViewerTool.RECTANGLE, ViewerTool.CIRCLE -> {
                                drawingStartPoint = startOffset
                                currentDrawing = com.tugi64.tugisline.data.model.DrawnShape(
                                    type = when (selectedTool) {
                                        ViewerTool.LINE -> com.tugi64.tugisline.data.model.ShapeType.LINE
                                        ViewerTool.RECTANGLE -> com.tugi64.tugisline.data.model.ShapeType.RECTANGLE
                                        ViewerTool.CIRCLE -> com.tugi64.tugisline.data.model.ShapeType.CIRCLE
                                        else -> com.tugi64.tugisline.data.model.ShapeType.LINE
                                    },
                                    startPoint = startOffset,
                                    color = Color.Cyan
                                )
                            }
                            else -> {
                                // PAN mode için varsayılan davranış
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        when (selectedTool) {
                            ViewerTool.LINE, ViewerTool.RECTANGLE, ViewerTool.CIRCLE -> {
                                // Çizim modu - şekli güncelle
                                drawingStartPoint?.let { start ->
                                    currentDrawing = currentDrawing?.copy(
                                        endPoint = change.position
                                    )
                                }
                            }
                            else -> {
                                // PAN modu - canvas'ı kaydır
                                offset += dragAmount
                                onViewStateChange(ViewState(zoom, offset.x, offset.y))
                            }
                        }
                        onCursorMove(change.position)
                    },
                    onDragEnd = {
                        // Çizim tamamlandı - listeye ekle
                        currentDrawing?.let { drawing ->
                            if (drawing.endPoint != null) {
                                drawnShapes = drawnShapes + drawing
                            }
                        }
                        currentDrawing = null
                        drawingStartPoint = null
                    }
                )
            }
    ) {
        val gridSize = 50f * zoom
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Grid çizimi
        for (i in 0 until (canvasWidth / gridSize).toInt() + 1) {
            val x = (i * gridSize + offset.x) % canvasWidth
            drawLine(
                color = Color(0xFF2A2A2A),
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = 1f
            )
        }

        for (i in 0 until (canvasHeight / gridSize).toInt() + 1) {
            val y = (i * gridSize + offset.y) % canvasHeight
            drawLine(
                color = Color(0xFF2A2A2A),
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f
            )
        }

        // Demo çizim - Basit geometriler
        val centerX = canvasWidth / 2 + offset.x
        val centerY = canvasHeight / 2 + offset.y

        // Dikdörtgen
        drawRect(
            color = Color.Cyan,
            topLeft = Offset(centerX - 100 * zoom, centerY - 80 * zoom),
            size = androidx.compose.ui.geometry.Size(200 * zoom, 160 * zoom),
            style = Stroke(width = 2f)
        )

        // Daire
        drawCircle(
            color = Color.Magenta,
            radius = 60f * zoom,
            center = Offset(centerX + 150 * zoom, centerY - 100 * zoom),
            style = Stroke(width = 2f)
        )

        // Çizgi
        drawLine(
            color = Color.Yellow,
            start = Offset(centerX - 150 * zoom, centerY + 100 * zoom),
            end = Offset(centerX + 150 * zoom, centerY + 100 * zoom),
            strokeWidth = 3f
        )

        // Merkez işareti
        drawLine(
            color = Color.Red,
            start = Offset(centerX - 10, centerY),
            end = Offset(centerX + 10, centerY),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Red,
            start = Offset(centerX, centerY - 10),
            end = Offset(centerX, centerY + 10),
            strokeWidth = 2f
        )

        // Çizilen şekilleri render et
        drawnShapes.forEach { shape ->
            when (shape.type) {
                com.tugi64.tugisline.data.model.ShapeType.LINE -> {
                    if (shape.endPoint != null) {
                        drawLine(
                            color = shape.color,
                            start = shape.startPoint,
                            end = shape.endPoint,
                            strokeWidth = shape.strokeWidth
                        )
                    }
                }
                com.tugi64.tugisline.data.model.ShapeType.RECTANGLE -> {
                    if (shape.endPoint != null) {
                        drawRect(
                            color = shape.color,
                            topLeft = Offset(
                                minOf(shape.startPoint.x, shape.endPoint.x),
                                minOf(shape.startPoint.y, shape.endPoint.y)
                            ),
                            size = androidx.compose.ui.geometry.Size(
                                kotlin.math.abs(shape.endPoint.x - shape.startPoint.x),
                                kotlin.math.abs(shape.endPoint.y - shape.startPoint.y)
                            ),
                            style = Stroke(width = shape.strokeWidth)
                        )
                    }
                }
                com.tugi64.tugisline.data.model.ShapeType.CIRCLE -> {
                    if (shape.endPoint != null) {
                        val radius = kotlin.math.sqrt(
                            (shape.endPoint.x - shape.startPoint.x).let { it * it } +
                            (shape.endPoint.y - shape.startPoint.y).let { it * it }
                        )
                        drawCircle(
                            color = shape.color,
                            radius = radius,
                            center = shape.startPoint,
                            style = Stroke(width = shape.strokeWidth)
                        )
                    }
                }
                else -> { /* Diğer şekiller için TODO */ }
            }
        }

        // Mevcut çizimi render et
        currentDrawing?.let { shape ->
            when (shape.type) {
                com.tugi64.tugisline.data.model.ShapeType.LINE -> {
                    if (shape.endPoint != null) {
                        drawLine(
                            color = shape.color.copy(alpha = 0.7f),
                            start = shape.startPoint,
                            end = shape.endPoint,
                            strokeWidth = shape.strokeWidth
                        )
                    }
                }
                com.tugi64.tugisline.data.model.ShapeType.RECTANGLE -> {
                    if (shape.endPoint != null) {
                        drawRect(
                            color = shape.color.copy(alpha = 0.7f),
                            topLeft = Offset(
                                minOf(shape.startPoint.x, shape.endPoint.x),
                                minOf(shape.startPoint.y, shape.endPoint.y)
                            ),
                            size = androidx.compose.ui.geometry.Size(
                                kotlin.math.abs(shape.endPoint.x - shape.startPoint.x),
                                kotlin.math.abs(shape.endPoint.y - shape.startPoint.y)
                            ),
                            style = Stroke(width = shape.strokeWidth)
                        )
                    }
                }
                com.tugi64.tugisline.data.model.ShapeType.CIRCLE -> {
                    if (shape.endPoint != null) {
                        val radius = kotlin.math.sqrt(
                            (shape.endPoint.x - shape.startPoint.x).let { it * it } +
                            (shape.endPoint.y - shape.startPoint.y).let { it * it }
                        )
                        drawCircle(
                            color = shape.color.copy(alpha = 0.7f),
                            radius = radius,
                            center = shape.startPoint,
                            style = Stroke(width = shape.strokeWidth)
                        )
                    }
                }
                else -> { /* Diğer şekiller için TODO */ }
            }
        }
    }
}

enum class ViewerTool {
    PAN,
    ZOOM_IN,
    ZOOM_OUT,
    ZOOM_WINDOW,
    ROTATE,
    SELECT,
    MEASURE_LINEAR,
    MEASURE_RADIAL,
    MEASURE_ANGLE,
    MEASURE_AREA,
    LINE,
    RECTANGLE,
    CIRCLE,
    ARC,
    TEXT
}

@Composable
fun LeftToolPalette(
    selectedTool: ViewerTool,
    onToolSelected: (ViewerTool) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ARAÇLAR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider()

            ToolButton(
                icon = Icons.Default.OpenWith,
                label = "Pan",
                isSelected = selectedTool == ViewerTool.PAN,
                onClick = { onToolSelected(ViewerTool.PAN) }
            )

            ToolButton(
                icon = Icons.Default.Add,
                label = "Zoom+",
                isSelected = selectedTool == ViewerTool.ZOOM_IN,
                onClick = { onToolSelected(ViewerTool.ZOOM_IN) }
            )

            ToolButton(
                icon = Icons.Default.HorizontalRule,
                label = "Zoom-",
                isSelected = selectedTool == ViewerTool.ZOOM_OUT,
                onClick = { onToolSelected(ViewerTool.ZOOM_OUT) }
            )

            ToolButton(
                icon = Icons.Default.CheckBoxOutlineBlank,
                label = "Seç",
                isSelected = selectedTool == ViewerTool.SELECT,
                onClick = { onToolSelected(ViewerTool.SELECT) }
            )

            HorizontalDivider()

            ToolButton(
                icon = Icons.Default.Timeline,
                label = "Ölç",
                isSelected = selectedTool == ViewerTool.MEASURE_LINEAR,
                onClick = { onToolSelected(ViewerTool.MEASURE_LINEAR) }
            )

            ToolButton(
                icon = Icons.Default.Circle,
                label = "Yarıçap",
                isSelected = selectedTool == ViewerTool.MEASURE_RADIAL,
                onClick = { onToolSelected(ViewerTool.MEASURE_RADIAL) }
            )

            ToolButton(
                icon = Icons.Default.TrendingUp,
                label = "Açı",
                isSelected = selectedTool == ViewerTool.MEASURE_ANGLE,
                onClick = { onToolSelected(ViewerTool.MEASURE_ANGLE) }
            )

            HorizontalDivider()

            ToolButton(
                icon = Icons.Default.ShowChart,
                label = "Çizgi",
                isSelected = selectedTool == ViewerTool.LINE,
                onClick = { onToolSelected(ViewerTool.LINE) }
            )

            ToolButton(
                icon = Icons.Default.CropSquare,
                label = "Dikdörtgen",
                isSelected = selectedTool == ViewerTool.RECTANGLE,
                onClick = { onToolSelected(ViewerTool.RECTANGLE) }
            )

            ToolButton(
                icon = Icons.Default.Circle,
                label = "Daire",
                isSelected = selectedTool == ViewerTool.CIRCLE,
                onClick = { onToolSelected(ViewerTool.CIRCLE) }
            )

            ToolButton(
                icon = Icons.Default.AutoAwesome,
                label = "Yay",
                isSelected = selectedTool == ViewerTool.ARC,
                onClick = { onToolSelected(ViewerTool.ARC) }
            )

            ToolButton(
                icon = Icons.Default.TextFields,
                label = "Metin",
                isSelected = selectedTool == ViewerTool.TEXT,
                onClick = { onToolSelected(ViewerTool.TEXT) }
            )
        }
    }
}

@Composable
fun ToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent
                )
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RightPropertiesPanel() {
    // Katman state'leri
    var layer1Visible by remember { mutableStateOf(true) }
    var layer1Locked by remember { mutableStateOf(false) }

    var layer2Visible by remember { mutableStateOf(true) }
    var layer2Locked by remember { mutableStateOf(false) }

    var layer3Visible by remember { mutableStateOf(true) }
    var layer3Locked by remember { mutableStateOf(false) }

    var layer4Visible by remember { mutableStateOf(false) }
    var layer4Locked by remember { mutableStateOf(false) }

    var layer5Visible by remember { mutableStateOf(true) }
    var layer5Locked by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "KATMANLAR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Demo Katmanlar - Çalışan butonlar
            LayerItem(
                name = "Duvarlar",
                color = Color.White,
                isVisible = layer1Visible,
                isLocked = layer1Locked,
                onToggleVisibility = { layer1Visible = !layer1Visible },
                onToggleLock = { layer1Locked = !layer1Locked }
            )
            LayerItem(
                name = "Kapılar",
                color = Color.Yellow,
                isVisible = layer2Visible,
                isLocked = layer2Locked,
                onToggleVisibility = { layer2Visible = !layer2Visible },
                onToggleLock = { layer2Locked = !layer2Locked }
            )
            LayerItem(
                name = "Pencereler",
                color = Color.Cyan,
                isVisible = layer3Visible,
                isLocked = layer3Locked,
                onToggleVisibility = { layer3Visible = !layer3Visible },
                onToggleLock = { layer3Locked = !layer3Locked }
            )
            LayerItem(
                name = "Mobilya",
                color = Color.Green,
                isVisible = layer4Visible,
                isLocked = layer4Locked,
                onToggleVisibility = { layer4Visible = !layer4Visible },
                onToggleLock = { layer4Locked = !layer4Locked }
            )
            LayerItem(
                name = "Ölçülendirme",
                color = Color.Red,
                isVisible = layer5Visible,
                isLocked = layer5Locked,
                onToggleVisibility = { layer5Visible = !layer5Visible },
                onToggleLock = { layer5Locked = !layer5Locked }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "ÖZELLİKLER",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            PropertyItem("Renk", "Beyaz")
            PropertyItem("Çizgi Tipi", "Sürekli")
            PropertyItem("Kalınlık", "0.25mm")
            PropertyItem("Şeffaflık", "%0")
        }
    }
}

@Composable
fun LayerItem(
    name: String,
    color: Color,
    isVisible: Boolean,
    isLocked: Boolean,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(onClick = onToggleVisibility, modifier = Modifier.size(32.dp)) {
            Icon(
                if (isVisible) Icons.Default.RemoveRedEye else Icons.Default.VisibilityOff,
                contentDescription = "Görünürlük",
                modifier = Modifier.size(20.dp),
                tint = if (isVisible) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onToggleLock, modifier = Modifier.size(32.dp)) {
            Icon(
                if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = "Kilit",
                modifier = Modifier.size(20.dp),
                tint = if (isLocked) MaterialTheme.colorScheme.error
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PropertyItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ViewerBottomBar(
    cursorPosition: Offset,
    scale: String,
    units: String,
    gridSnap: Boolean,
    onToggleGridSnap: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Koordinat Bilgisi
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CoordinateDisplay("X", String.format("%.2f", cursorPosition.x))
                CoordinateDisplay("Y", String.format("%.2f", cursorPosition.y))
                CoordinateDisplay("Z", "0.00")
            }

            // Çizim Bilgileri
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip("Ölçek: $scale")
                InfoChip("Birim: $units")
                FilterChip(
                    selected = gridSnap,
                    onClick = onToggleGridSnap,
                    label = { Text("Grid Snap") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Grid4x4,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CoordinateDisplay(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$label:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SaveDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.SaveAlt, contentDescription = null) },
        title = { Text("Kaydet") },
        text = {
            Text("Dosya başarıyla kaydedildi!\n\nKonum: /storage/emulated/0/Documents/Mimari_Plan_A1.dwg")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}

@Composable
fun ShareDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Share, contentDescription = null) },
        title = { Text("Paylaş") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Dosyayı paylaşmak için bir yöntem seçin:")
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("E-posta")
                }
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Bulut Depolama")
                }
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Link, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Link Oluştur")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun PrintDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Send, contentDescription = null) },
        title = { Text("Yazdır") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Yazdırma Ayarları:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kağıt Boyutu:")
                    Text("A4", fontWeight = FontWeight.SemiBold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Yönelim:")
                    Text("Yatay", fontWeight = FontWeight.SemiBold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ölçek:")
                    Text("1:100", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Yazdır")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

