package com.tugi64.tugisline.ui.screens.mainmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tugi64.tugisline.data.model.DWGFile
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToFileExplorer: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToCloud: () -> Unit,
    onOpenFile: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var showNewProjectDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Son Açılanlar", "Sık Kullanılanlar", "Yerel Dosyalar")

    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onConfirm = { projectName ->
                showNewProjectDialog = false
                // Yeni proje ile viewer'ı aç
                onOpenFile("new_project")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "TugiSline CAD",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Professional DWG Viewer",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHelp) {
                        Icon(Icons.Default.Info, contentDescription = "Yardım")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Arama Çubuğu
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier.padding(16.dp)
            )

            // Hızlı Erişim Kartları
            QuickAccessCards(
                onNewProject = { showNewProjectDialog = true },
                onOpenFile = onNavigateToFileExplorer,
                onCloudConnect = onNavigateToCloud,
                onRecent = { selectedTab = 0 },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Seçici
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Dosya Listesi
            FileListSection(
                selectedTab = selectedTab,
                searchQuery = searchQuery,
                onOpenFile = onOpenFile,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Dosya ara...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Ara")
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Temizle")
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        singleLine = true
    )
}

@Composable
fun QuickAccessCards(
    onNewProject: () -> Unit,
    onOpenFile: () -> Unit,
    onCloudConnect: () -> Unit,
    onRecent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cards = listOf(
        QuickAccessItem("Yeni Proje", Icons.Default.Add, MaterialTheme.colorScheme.primary, onNewProject),
        QuickAccessItem("Dosya Aç", Icons.Default.Folder, MaterialTheme.colorScheme.secondary, onOpenFile),
        QuickAccessItem("Bulut", Icons.Default.CloudQueue, MaterialTheme.colorScheme.tertiary, onCloudConnect),
        QuickAccessItem("Son Dosyalar", Icons.Default.AccessTime, MaterialTheme.colorScheme.error, onRecent)
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cards) { item ->
            QuickAccessCard(item)
        }
    }
}

data class QuickAccessItem(
    val title: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

@Composable
fun QuickAccessCard(item: QuickAccessItem) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp)
            .clickable(onClick = item.onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = item.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(40.dp),
                tint = item.color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = item.color
            )
        }
    }
}

@Composable
fun FileListSection(
    selectedTab: Int,
    searchQuery: String,
    onOpenFile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Demo veriler
    var sampleFiles by remember {
        mutableStateOf(
            listOf(
                DWGFile("1", "Mimari_Plan_A1.dwg", "/storage/emulated/0/", 2048576, System.currentTimeMillis() - 3600000),
                DWGFile("2", "Elektrik_Projesi.dwg", "/storage/emulated/0/", 1024000, System.currentTimeMillis() - 7200000),
                DWGFile("3", "Mekanik_Detay.dwg", "/storage/emulated/0/", 3145728, System.currentTimeMillis() - 86400000),
                DWGFile("4", "Site_Plani.dwg", "/storage/emulated/0/", 5242880, System.currentTimeMillis() - 172800000),
                DWGFile("5", "Cephe_Gorunusu.dwg", "/storage/emulated/0/", 4194304, System.currentTimeMillis() - 259200000)
            )
        )
    }

    val filteredFiles = remember(searchQuery, selectedTab, sampleFiles) {
        val filtered = sampleFiles.filter { file ->
            file.name.contains(searchQuery, ignoreCase = true)
        }

        // Tab'a göre filtrele
        when (selectedTab) {
            0 -> filtered.sortedByDescending { it.lastModified } // Son Açılanlar
            1 -> filtered.filter { it.isFavorite } // Sık Kullanılanlar
            2 -> filtered.sortedBy { it.name } // Yerel Dosyalar (alfabetik)
            else -> filtered
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredFiles) { file ->
            FileItem(
                file = file,
                onOpenFile = { onOpenFile(file.id) },
                onToggleFavorite = {
                    sampleFiles = sampleFiles.map {
                        if (it.id == file.id) it.copy(isFavorite = !it.isFavorite) else it
                    }
                },
                onShowMenu = { /* TODO: Show context menu */ }
            )
        }

        if (filteredFiles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Dosya bulunamadı",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(
    file: DWGFile,
    onOpenFile: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowMenu: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenFile),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dosya ikonu
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Dosya bilgileri
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = formatFileSize(file.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDate(file.lastModified),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Favori butonu
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (file.isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Favori",
                    tint = if (file.isFavorite) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Menü butonu
            IconButton(onClick = onShowMenu) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Menü",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("tr"))
    return sdf.format(Date(timestamp))
}

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf("Boş Proje") }

    val templates = listOf(
        "Boş Proje",
        "Mimari Plan Şablonu",
        "Elektrik Projesi Şablonu",
        "Mekanik Proje Şablonu"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        title = { Text("Yeni Proje Oluştur") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Proje Adı") },
                    placeholder = { Text("Örn: Mimari_Plan_2024") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Şablon Seçin:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    templates.forEach { template ->
                        FilterChip(
                            selected = selectedTemplate == template,
                            onClick = { selectedTemplate = template },
                            label = { Text(template) },
                            leadingIcon = if (selectedTemplate == template) {
                                {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(projectName.ifBlank { "Yeni_Proje" }) },
                enabled = true
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Oluştur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
