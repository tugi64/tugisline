package com.tugi64.tugisline.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    var showLicenseDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    if (showLicenseDialog) {
        LicenseDialog(onDismiss = { showLicenseDialog = false })
    }

    if (showPrivacyDialog) {
        PrivacyDialog(onDismiss = { showPrivacyDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hakkında", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Icon(
                    Icons.Default.Architecture,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    "TugiSline CAD",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    "Professional DWG Viewer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        "Versiyon 1.0.0",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoRow(Icons.Default.Person, "Geliştirici", "Tugi64")
                        InfoRow(Icons.Default.Email, "E-posta", "info@tugisline.com")
                        InfoRow(Icons.Default.Language, "Web", "www.tugisline.com")
                        InfoRow(Icons.Default.Code, "GitHub", "github.com/tugi64/tugisline")
                    }
                }
            }

            item {
                Text(
                    "Modern, hızlı ve kullanıcı dostu CAD dosya görüntüleyici. " +
                            "DWG, DXF ve daha fazla formatı destekler.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { showLicenseDialog = true },
                        label = { Text("Lisans") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    AssistChip(
                        onClick = { showPrivacyDialog = true },
                        label = { Text("Gizlilik") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.PrivacyTip,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            item {
                Text(
                    "© 2027 TugiSline. Tüm hakları saklıdır.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LicenseDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Description, contentDescription = null) },
        title = { Text("Lisans Bilgisi") },
        text = {
            Column {
                Text(
                    "TugiSline CAD - Professional DWG Viewer\n\n" +
                    "Copyright © 2027 Tugi64\n\n" +
                    "Bu yazılım MIT Lisansı altında lisanslanmıştır.\n\n" +
                    "İzin verilir: Ticari kullanım, değiştirme, dağıtım, özel kullanım.\n\n" +
                    "Koşullar: Lisans ve telif hakkı bildirimi dahil edilmelidir.\n\n" +
                    "Garanti yoktur ve sorumluluk kabul edilmez.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.PrivacyTip, contentDescription = null) },
        title = { Text("Gizlilik Politikası") },
        text = {
            Column {
                Text(
                    "Veri Toplama:\n" +
                    "• Uygulama, kişisel verilerinizi toplamaz\n" +
                    "• Dosyalarınız cihazınızda kalır\n" +
                    "• İnternet bağlantısı sadece bulut servisleri için kullanılır\n\n" +
                    "İzinler:\n" +
                    "• Depolama: CAD dosyalarına erişim için\n" +
                    "• İnternet: Bulut senkronizasyonu için (opsiyonel)\n\n" +
                    "Güvenlik:\n" +
                    "• Tüm veriler cihazınızda şifrelenir\n" +
                    "• Üçüncü taraflarla veri paylaşımı yapılmaz\n\n" +
                    "İletişim: info@tugisline.com",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Anladım")
            }
        }
    )
}

