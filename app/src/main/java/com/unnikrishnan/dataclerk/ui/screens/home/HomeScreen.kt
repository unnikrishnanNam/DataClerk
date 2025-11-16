package com.unnikrishnan.dataclerk.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unnikrishnan.dataclerk.data.models.*
import com.unnikrishnan.dataclerk.ui.components.*
import com.unnikrishnan.dataclerk.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToChatWithId: (String, Long) -> Unit,
    onNavigateToSchema: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Collect state from ViewModel
    val databasesState by viewModel.databases.collectAsState()
    val selectedDatabase by viewModel.selectedDatabase.collectAsState()
    val databaseInfoState by viewModel.databaseInfo.collectAsState()
    val recentChats by viewModel.recentChats.collectAsState()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onNavigateToSettings = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToSettings()
                    }
                },
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                
                // Database Selector
                item {
                    when (databasesState) {
                        is UiState.Loading -> {
                            ShimmerBox(height = 80)
                        }
                        is UiState.Success -> {
                            val databases = (databasesState as UiState.Success<List<String>>).data
                            DatabaseSelector(
                                selectedDatabase = selectedDatabase ?: databases.firstOrNull() ?: "",
                                databases = databases,
                                onDatabaseSelected = { viewModel.selectDatabase(it) }
                            )
                        }
                        is UiState.Error -> {
                            Text(
                                text = "Failed to load databases",
                                color = AccentError,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        else -> {}
                    }
                }
                
                // Database Info Card
                item {
                    when (databaseInfoState) {
                        is UiState.Loading -> {
                            ShimmerBox(height = 120)
                        }
                        is UiState.Success -> {
                            val info = (databaseInfoState as UiState.Success<DatabaseInfo>).data
                            DatabaseCard(
                                databaseInfo = info,
                                onClick = { /* Navigate to database details */ }
                            )
                        }
                        is UiState.Error -> {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = AccentError.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Failed to load database info",
                                    color = AccentError,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
                
                // Quick Actions
                item {
                    QuickActions(
                        onViewSchema = { 
                            selectedDatabase?.let { onNavigateToSchema(it) }
                        },
                        onNewChat = { 
                            selectedDatabase?.let { onNavigateToChat(it) }
                        },
                        onHistory = {
                            selectedDatabase?.let { onNavigateToHistory(it) }
                        }
                    )
                }
                
                // Recent Chats Section
                item {
                    Text(
                        text = "Recent Conversations",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }
                
                if (recentChats.isEmpty()) {
                    item {
                        EmptyRecentChatsState()
                    }
                } else {
                    items(recentChats) { chat ->
                        RecentChatItem(
                            chat = chat,
                            onClick = { 
                                onNavigateToChatWithId(
                                    selectedDatabase ?: "",
                                    chat.id.toLong()
                                )
                            }
                        )
                    }
                }
                
                // Chat with Database Button
                item {
                    PillButton(
                        text = "Chat with Database",
                        onClick = { 
                            selectedDatabase?.let { onNavigateToChat(it) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Chat,
                        backgroundColor = AccentPrimary,
                        enabled = selectedDatabase != null
                    )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Data Clerk",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Your Database Assistant",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatabaseSelector(
    selectedDatabase: String,
    databases: List<String>,
    onDatabaseSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = ShadowDark,
                spotColor = ShadowDark
            ),
        color = SurfaceElevated1,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Database",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    color = SurfaceElevated2,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = AccentPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedDatabase,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(SurfaceElevated1)
                ) {
                    databases.forEach { database ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = database,
                                    color = TextPrimary
                                )
                            },
                            onClick = {
                                onDatabaseSelected(database)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = if (database == selectedDatabase) AccentPrimary else TextSecondary
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActions(
    onViewSchema: () -> Unit,
    onNewChat: () -> Unit,
    onHistory: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                IconPillButton(
                    text = "View Schema",
                    icon = Icons.Default.TableChart,
                    onClick = onViewSchema
                )
            }
            item {
                IconPillButton(
                    text = "New Chat",
                    icon = Icons.Default.Add,
                    onClick = onNewChat
                )
            }
            item {
                IconPillButton(
                    text = "History",
                    icon = Icons.Default.History,
                    onClick = onHistory
                )
            }
        }
    }
}

@Composable
private fun EmptyRecentChatsState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        color = SurfaceElevated1,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "💬",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No conversations yet",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start chatting with your database to see your conversation history here",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}


@Composable
private fun RecentChatItem(
    chat: RecentChat,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = ShadowDark,
                spotColor = ShadowDark
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = SurfaceElevated1,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = AccentPrimary.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${chat.messageCount} messages",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationDrawerContent(
    onNavigateToSettings: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Data Clerk",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 16.dp))
            
            // User Profile Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceElevated1,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        color = AccentSecondary,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Business Owner",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "owner@business.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Menu Items
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                text = "Settings",
                onClick = onNavigateToSettings
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Info,
                text = "About",
                onClick = { /* TODO */ }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Help,
                text = "Help & Support",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer
            Text(
                text = "© 2025 Data Clerk",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}
