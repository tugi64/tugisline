package com.tugi64.tugisline.ui.screens.cloud

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudConnectScreen(
    onNavigateBack: () -> Unit
) {
    var cloudProviders by remember {
        mutableStateOf(
            listOf(
                CloudProviderItem("Google Drive", Icons.Default.CloudUpload, false),
                CloudProviderItem("Dropbox", Icons.Default.CloudDownload, false),
                CloudProviderItem("OneDrive", Icons.Default.CloudQueue, false),
                CloudProviderItem("FTP", Icons.Default.Storage, false),
                CloudProviderItem("SFTP", Icons.Default.Lock, false)
            )
        )
    }

    var showConnectionDialog by remember { mutableStateOf(false) }
    var selectedProvider by remember { mutableStateOf<CloudProviderItem?>(null) }

    if (showConnectionDialog && selectedProvider != null) {
        ConnectionDialog(
            provider = selectedProvider!!,
            onDismiss = { showConnectionDialog = false },
            onConfirm = {
                cloudProviders = cloudProviders.map { provider ->
                    if (provider.name == selectedProvider?.name) {
                        provider.copy(isConnected = !provider.isConnected)
                    } else provider
                }
                showConnectionDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bulut Bağlantıları", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Dosyalarınızı bulut depolamaya bağlayın ve her yerden erişin",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            items(cloudProviders) { provider ->
                CloudProviderCard(
                    provider = provider,
                    onConnect = {
                        selectedProvider = provider
                        showConnectionDialog = true
                    }
                )
            }
        }
    }
}

data class CloudProviderItem(
    val name: String,
    val icon: ImageVector,
    val isConnected: Boolean
)

@Composable
fun ConnectionDialog(
    provider: CloudProviderItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(provider.icon, contentDescription = null) },
        title = {
            Text(if (provider.isConnected) "Bağlantıyı Kes" else "Bağlan")
        },
        text = {
            Text(
                if (provider.isConnected) {
                    "${provider.name} bağlantısını kesmek istediğinizden emin misiniz?"
                } else {
                    "${provider.name} ile bağlantı kurmak için hesap bilgilerinizi girin.\n\n" +
                    "Not: Bu demo sürümünde gerçek bağlantı yapılmayacaktır."
                }
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = if (provider.isConnected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(if (provider.isConnected) "Bağlantıyı Kes" else "Bağlan")
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
fun CloudProviderCard(
    provider: CloudProviderItem,
    onConnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        provider.icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    provider.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (provider.isConnected) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Bağlı",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Text(
                        "Bağlı değil",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onConnect,
                colors = if (provider.isConnected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(if (provider.isConnected) "Bağlantıyı Kes" else "Bağlan")
            }
        }
    }
}

