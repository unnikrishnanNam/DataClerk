package com.unnikrishnan.dataclerk.ui.screens.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unnikrishnan.dataclerk.data.models.*
import com.unnikrishnan.dataclerk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    databaseName: String,
    geminiApiKey: String?,
    conversationId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: ((String) -> Unit)? = null,
    viewModel: ChatViewModel = viewModel()
) {
    // Initialize ViewModel with conversationId
    LaunchedEffect(databaseName, conversationId) {
        viewModel.initialize(databaseName, geminiApiKey, conversationId)
    }
    
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            ChatTopBar(
                databaseName = databaseName,
                onNavigateBack = onNavigateBack,
                onNavigateToHistory = { onNavigateToHistory?.invoke(databaseName) },
                onClearChat = { viewModel.clearChat() }
            )
        },
        bottomBar = {
            ChatInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank() && !isProcessing) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                },
                isProcessing = isProcessing
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (messages.isEmpty()) {
            EmptyChatState(
                hasApiKey = geminiApiKey != null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    databaseName: String,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onClearChat: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Chat",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = databaseName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToHistory) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "View history",
                    tint = TextSecondary
                )
            }
            IconButton(onClick = onClearChat) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Clear chat",
                    tint = TextSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isProcessing: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                ambientColor = ShadowDark,
                spotColor = ShadowDark
            ),
        color = SurfaceDark,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                color = SurfaceElevated1,
                shape = RoundedCornerShape(24.dp)
            ) {
                TextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Ask about your data...",
                            color = TextTertiary
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onSendMessage() }
                    ),
                    maxLines = 4
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape
                    ),
                color = if (messageText.isBlank() || isProcessing) SurfaceElevated1 else AccentPrimary,
                shape = CircleShape
            ) {
                IconButton(
                    onClick = onSendMessage,
                    enabled = messageText.isNotBlank() && !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TextTertiary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (messageText.isBlank()) TextTertiary else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.role == MessageRole.USER)
            Arrangement.End else Arrangement.Start
    ) {
        if (message.role == MessageRole.ASSISTANT) {
            Surface(
                modifier = Modifier.size(36.dp),
                color = if (message.contentType == MessageContentType.ERROR)
                    AccentError.copy(alpha = 0.15f) else AccentPrimary.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (message.contentType == MessageContentType.ERROR)
                            Icons.Default.Error else Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = if (message.contentType == MessageContentType.ERROR) AccentError else AccentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Surface(
                modifier = Modifier.widthIn(max = if (message.contentType in listOf(
                        MessageContentType.TABLE,
                        MessageContentType.CHART
                    )) 340.dp else 280.dp),
                color = when {
                    message.role == MessageRole.USER -> AccentPrimary
                    message.contentType == MessageContentType.ERROR -> AccentError.copy(alpha = 0.15f)
                    else -> SurfaceElevated1
                },
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (message.role == MessageRole.USER) 20.dp else 4.dp,
                    bottomEnd = if (message.role == MessageRole.USER) 4.dp else 20.dp
                ),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (message.contentType) {
                        MessageContentType.TEXT, MessageContentType.ERROR -> {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (message.role == MessageRole.USER) Color.White
                                else if (message.contentType == MessageContentType.ERROR) AccentError
                                else TextPrimary
                            )
                        }
                        MessageContentType.TABLE -> {
                            if (message.content.isNotBlank()) {
                                Text(
                                    text = message.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            message.tableData?.let { TableView(tableData = it) }
                        }
                        MessageContentType.CHART -> {
                            if (message.content.isNotBlank()) {
                                Text(
                                    text = message.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            message.chartData?.let { ChartView(chartData = it) }
                        }
                        else -> {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                    if (message.metadata != null && message.role == MessageRole.ASSISTANT && message.contentType != MessageContentType.ERROR) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MessageMetadataView(metadata = message.metadata)
                    }
                }
            }
        }
        if (message.role == MessageRole.USER) {
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(36.dp),
                color = AccentSecondary.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AccentSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageMetadataView(metadata: MessageMetadata) {
    Surface(
        color = BackgroundDark.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            metadata.query?.let { query ->
                Text(
                    text = "Query: $query",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                metadata.rowCount?.let { count ->
                    Text(
                        text = "$count rows",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                
                metadata.executionTime?.let { time ->
                    Text(
                        text = "${time}ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChatState(
    hasApiKey: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasApiKey) 
                Icons.Default.ChatBubbleOutline 
            else 
                Icons.Default.Key,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = if (hasApiKey) TextTertiary else AccentWarning
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (hasApiKey) 
                "Start a conversation" 
            else 
                "API Key Required",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasApiKey)
                "Ask questions about your database\nand get instant insights"
            else
                "Please configure your Gemini API key\nin Settings to enable chat features",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TableView(
    tableData: TableData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SurfaceElevated2,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        // Horizontal scroll for wide tables
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            item {
                // Headers column
                Column(
                    modifier = Modifier.background(AccentPrimary.copy(alpha = 0.15f))
                ) {
                    tableData.headers.forEach { header ->
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            
            // Data columns
            items(tableData.rows.size) { rowIndex ->
                Column(
                    modifier = Modifier.background(
                        if (rowIndex % 2 == 0) SurfaceElevated1 else SurfaceElevated2
                    )
                ) {
                    tableData.rows[rowIndex].forEach { cell ->
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = cell,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
        
        // Row count indicator
        if (tableData.rows.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${tableData.rows.size} rows",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun ChartView(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    when (chartData.type) {
        ChartType.BAR -> BarChartView(chartData, modifier)
        ChartType.LINE -> LineChartView(chartData, modifier)
        ChartType.PIE -> PieChartView(chartData, modifier)
    }
}

@Composable
private fun BarChartView(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    val maxValue = chartData.values.maxOrNull()?.takeIf { it > 0f } ?: 1f
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                color = SurfaceElevated2,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            chartData.values.forEach { value ->
                val barHeight = (value / maxValue).coerceIn(0f, 1f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%.1f".format(value),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(barHeight)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        AccentPrimary,
                                        AccentPrimary.copy(alpha = 0.6f)
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            chartData.labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LineChartView(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    val maxValue = chartData.values.maxOrNull() ?: 1f
    val minValue = chartData.values.minOrNull() ?: 0f
    val range = maxValue - minValue
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                color = SurfaceElevated2,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        // Chart area
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val spacing = canvasWidth / (chartData.values.size - 1).coerceAtLeast(1)
            
            // Draw grid lines
            repeat(5) { i ->
                val y = canvasHeight * i / 4f
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f
                )
            }
            
            // Draw line chart
            val path = Path()
            chartData.values.forEachIndexed { index, value ->
                val x = index * spacing
                val normalizedValue = if (range > 0) (value - minValue) / range else 0.5f
                val y = canvasHeight - (normalizedValue * canvasHeight)
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                
                // Draw point
                drawCircle(
                    color = AccentPrimary,
                    radius = 6f,
                    center = Offset(x, y)
                )
            }
            
            // Draw line
            drawPath(
                path = path,
                color = AccentPrimary,
                style = Stroke(width = 3f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            chartData.labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PieChartView(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    val totalRaw = chartData.values.sum()
    val total = if (totalRaw > 0f) totalRaw else 1f
    val colors = listOf(
        AccentPrimary,
        AccentSecondary,
        AccentSuccess,
        AccentWarning,
        Color(0xFF9C27B0),
        Color(0xFF00BCD4)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = SurfaceElevated2,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pie chart
        Canvas(
            modifier = Modifier
                .size(160.dp)
                .weight(1f)
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            
            var startAngle = -90f
            chartData.values.forEachIndexed { index, value ->
                val sweepAngle = (value / total) * 360f
                
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                
                startAngle += sweepAngle
            }
            
            // Center white circle for donut effect
            drawCircle(
                color = SurfaceElevated2,
                radius = radius * 0.5f,
                center = center
            )
        }
        
        // Legend
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chartData.labels.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = colors[index % colors.size],
                                shape = CircleShape
                            )
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "%.1f%%".format((chartData.values[index] / total) * 100),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
