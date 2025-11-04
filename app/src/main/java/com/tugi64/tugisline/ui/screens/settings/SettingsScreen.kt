package com.tugi64.tugisline.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(true) }
    var gridSize by remember { mutableStateOf(50f) }
    var autoSave by remember { mutableStateOf(true) }
    var autoSaveInterval by remember { mutableStateOf(5f) }
    var hardwareAcceleration by remember { mutableStateOf(true) }
    var units by remember { mutableStateOf("Metrik") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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
            // Görünüm Ayarları
            item {
                SettingsSectionTitle("Görünüm Ayarları")
            }

            item {
                SettingsSwitchItem(
                    title = "Koyu Tema",
                    subtitle = "Karanlık mod kullan",
                    icon = Icons.Default.DarkMode,
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            item {
                SettingsSwitchItem(
                    title = "Izgara Göster",
                    subtitle = "Çizim ızgarasını göster",
                    icon = Icons.Default.GridOn,
                    checked = showGrid,
                    onCheckedChange = { showGrid = it }
                )
            }

            item {
                SettingsSliderItem(
                    title = "Izgara Boyutu",
                    subtitle = "${gridSize.toInt()} piksel",
                    icon = Icons.Default.AspectRatio,
                    value = gridSize,
                    valueRange = 10f..100f,
                    onValueChange = { gridSize = it }
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Dosya Ayarları
            item {
                SettingsSectionTitle("Dosya Ayarları")
            }

            item {
                SettingsSwitchItem(
                    title = "Otomatik Kaydetme",
                    subtitle = "Değişiklikleri otomatik kaydet",
                    icon = Icons.Default.Save,
                    checked = autoSave,
                    onCheckedChange = { autoSave = it }
                )
            }

            item {
                SettingsSliderItem(
                    title = "Kaydetme Aralığı",
                    subtitle = "${autoSaveInterval.toInt()} dakika",
                    icon = Icons.Default.Timer,
                    value = autoSaveInterval,
                    valueRange = 1f..30f,
                    onValueChange = { autoSaveInterval = it },
                    enabled = autoSave
                )
            }

            item {
                SettingsClickItem(
                    title = "Varsayılan Kayıt Konumu",
                    subtitle = "/storage/emulated/0/Documents",
                    icon = Icons.Default.Folder,
                    onClick = { /* TODO */ }
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Ölçüm Ayarları
            item {
                SettingsSectionTitle("Ölçüm Ayarları")
            }

            item {
                SettingsDropdownItem(
                    title = "Birim Sistemi",
                    subtitle = units,
                    icon = Icons.Default.Straighten,
                    options = listOf("Metrik", "İmparatorluk"),
                    selectedOption = units,
                    onOptionSelected = { units = it }
                )
            }

            item {
                SettingsClickItem(
                    title = "Hassasiyet",
                    subtitle = "2 ondalık basamak",
                    icon = Icons.Default.Edit,
                    onClick = { /* TODO */ }
                )
            }

            item {
                SettingsClickItem(
                    title = "Açı Formatı",
                    subtitle = "Derece",
                    icon = Icons.Default.Architecture,
                    onClick = { /* TODO */ }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // Performans Ayarları
            item {
                SettingsSectionTitle("Performans Ayarları")
            }

            item {
                SettingsSwitchItem(
                    title = "Donanım Hızlandırma",
                    subtitle = "GPU kullanarak render et",
                    icon = Icons.Default.Speed,
                    checked = hardwareAcceleration,
                    onCheckedChange = { hardwareAcceleration = it }
                )
            }

            item {
                SettingsClickItem(
                    title = "Önbellek Boyutu",
                    subtitle = "256 MB",
                    icon = Icons.Default.Storage,
                    onClick = { /* TODO */ }
                )
            }

            item {
                SettingsClickItem(
                    title = "Render Kalitesi",
                    subtitle = "Yüksek",
                    icon = Icons.Default.Image,
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingsClickItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsDropdownItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

