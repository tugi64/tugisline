package com.tugi64.tugisline.ui.screens.layers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tugi64.tugisline.data.model.Layer
import com.tugi64.tugisline.data.model.LineType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerManagerScreen(
    onNavigateBack: () -> Unit
) {
    var sampleLayers by remember {
        mutableStateOf(
            listOf(
                Layer("1", "Duvarlar", true, false, false, Color.White, LineType.CONTINUOUS, 0.5f),
                Layer("2", "Kapılar", true, false, false, Color.Yellow, LineType.CONTINUOUS, 0.35f),
                Layer("3", "Pencereler", true, false, false, Color.Cyan, LineType.CONTINUOUS, 0.35f),
                Layer("4", "Mobilya", false, false, false, Color.Green, LineType.DASHED, 0.25f),
                Layer("5", "Ölçülendirme", true, true, false, Color.Red, LineType.CONTINUOUS, 0.18f),
                Layer("6", "Elektrik", true, false, false, Color.Magenta, LineType.DASHDOT, 0.25f),
                Layer("7", "Tesisat", false, false, true, Color.Blue, LineType.CONTINUOUS, 0.25f)
            )
        )
    }

    var showAddLayerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showEditLayerDialog by remember { mutableStateOf(false) }
    var layerToDelete by remember { mutableStateOf<Layer?>(null) }
    var layerToEdit by remember { mutableStateOf<Layer?>(null) }

    if (showAddLayerDialog) {
        AddLayerDialog(
            onDismiss = { showAddLayerDialog = false },
            onConfirm = { layerName ->
                val newLayer = Layer(
                    id = (sampleLayers.size + 1).toString(),
                    name = layerName,
                    color = Color.White
                )
                sampleLayers = sampleLayers + newLayer
                showAddLayerDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog && layerToDelete != null) {
        DeleteLayerDialog(
            layer = layerToDelete!!,
            onDismiss = { showDeleteConfirmDialog = false },
            onConfirm = {
                sampleLayers = sampleLayers.filter { it.id != layerToDelete?.id }
                showDeleteConfirmDialog = false
                layerToDelete = null
            }
        )
    }

    if (showEditLayerDialog && layerToEdit != null) {
        EditLayerDialog(
            layer = layerToEdit!!,
            onDismiss = { showEditLayerDialog = false },
            onConfirm = { updatedLayer ->
                sampleLayers = sampleLayers.map {
                    if (it.id == updatedLayer.id) updatedLayer else it
                }
                showEditLayerDialog = false
                layerToEdit = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katman Yöneticisi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddLayerDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Yeni Katman")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleLayers) { layer ->
                LayerCard(
                    layer = layer,
                    onToggleVisibility = {
                        sampleLayers = sampleLayers.map {
                            if (it.id == layer.id) it.copy(isVisible = !it.isVisible) else it
                        }
                    },
                    onToggleLock = {
                        sampleLayers = sampleLayers.map {
                            if (it.id == layer.id) it.copy(isLocked = !it.isLocked) else it
                        }
                    },
                    onToggleFreeze = {
                        sampleLayers = sampleLayers.map {
                            if (it.id == layer.id) it.copy(isFrozen = !it.isFrozen) else it
                        }
                    },
                    onEdit = {
                        layerToEdit = layer
                        showEditLayerDialog = true
                    },
                    onDelete = {
                        layerToDelete = layer
                        showDeleteConfirmDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun LayerCard(
    layer: Layer,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleFreeze: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Renk göstergesi
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(layer.color)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Katman adı
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = layer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${layer.lineType} • ${layer.lineWeight}mm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Kontrol butonları
                Row {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            if (layer.isVisible) Icons.Default.RemoveRedEye else Icons.Default.VisibilityOff,
                            contentDescription = "Görünürlük",
                            tint = if (layer.isVisible) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onToggleLock) {
                        Icon(
                            if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Kilit",
                            tint = if (layer.isLocked) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            if (layer.isFrozen) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AcUnit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Dondurulmuş katman",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddLayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var layerName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        title = { Text("Yeni Katman Ekle") },
        text = {
            OutlinedTextField(
                value = layerName,
                onValueChange = { layerName = it },
                label = { Text("Katman Adı") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(layerName) },
                enabled = layerName.isNotBlank()
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun DeleteLayerDialog(
    layer: Layer,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Katmanı Sil") },
        text = {
            Text("'${layer.name}' katmanını silmek istediğinizden emin misiniz?\n\nBu işlem geri alınamaz.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sil")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun EditLayerDialog(
    layer: Layer,
    onDismiss: () -> Unit,
    onConfirm: (Layer) -> Unit
) {
    var layerName by remember { mutableStateOf(layer.name) }
    var selectedLineType by remember { mutableStateOf(layer.lineType) }
    var lineWeight by remember { mutableStateOf(layer.lineWeight) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Edit, contentDescription = null) },
        title = { Text("Katmanı Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = layerName,
                    onValueChange = { layerName = it },
                    label = { Text("Katman Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Çizgi Tipi:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LineType.values().take(3).forEach { type ->
                        FilterChip(
                            selected = selectedLineType == type,
                            onClick = { selectedLineType = type },
                            label = { Text(type.name) }
                        )
                    }
                }

                Text("Çizgi Kalınlığı: ${String.format("%.2f", lineWeight)}mm",
                     style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = lineWeight,
                    onValueChange = { lineWeight = it },
                    valueRange = 0.1f..2f,
                    steps = 18
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(layer.copy(
                        name = layerName,
                        lineType = selectedLineType,
                        lineWeight = lineWeight
                    ))
                },
                enabled = layerName.isNotBlank()
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

