package com.tugi64.tugisline.ui.screens.help

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    val helpTopics = remember {
        listOf(
            HelpTopic(
                "Başlarken",
                "Uygulamayı kullanmaya başlama rehberi",
                Icons.Default.PlayArrow
            ),
            HelpTopic(
                "Dosya Açma",
                "DWG/DXF dosyalarını nasıl açarım?",
                Icons.Default.Folder
            ),
            HelpTopic(
                "Görüntüleme Araçları",
                "Zoom, Pan ve diğer görüntüleme araçları",
                Icons.Default.ZoomIn
            ),
            HelpTopic(
                "Ölçüm Araçları",
                "Mesafe, açı ve alan ölçümleri",
                Icons.Default.Straighten
            ),
            HelpTopic(
                "Katman Yönetimi",
                "Katmanları görüntüleme ve yönetme",
                Icons.Default.Layers
            ),
            HelpTopic(
                "Bulut Bağlantısı",
                "Bulut depolamaya bağlanma",
                Icons.Default.CloudQueue
            ),
            HelpTopic(
                "Ayarlar",
                "Uygulama ayarlarını özelleştirme",
                Icons.Default.Settings
            ),
            HelpTopic(
                "Dokunma Jestleri",
                "Mobil kontroller ve jestler",
                Icons.Default.TouchApp
            ),
            HelpTopic(
                "Sık Sorulan Sorular",
                "En çok sorulan sorular ve cevapları",
                Icons.Default.QuestionAnswer
            ),
            HelpTopic(
                "İletişim",
                "Destek ekibiyle iletişime geçin",
                Icons.Default.Email
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yardım & Destek", fontWeight = FontWeight.Bold) },
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
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Hızlı İpucu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "İki parmakla yakınlaştırma/uzaklaştırma yapabilirsiniz",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(helpTopics) { topic ->
                HelpTopicCard(topic) {
                    // TODO: Navigate to detailed help
                }
            }
        }
    }
}

data class HelpTopic(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun HelpTopicCard(
    topic: HelpTopic,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        topic.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    topic.description,
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

