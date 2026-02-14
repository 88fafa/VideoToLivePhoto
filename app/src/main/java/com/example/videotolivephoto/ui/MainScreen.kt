package com.example.videotolivephoto.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    
    // 视频选择器
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectVideo(it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("视频转动态照片") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 视频选择区域
            VideoSelectorCard(
                hasVideo = uiState.selectedVideoUri != null,
                videoInfo = uiState.videoInfo,
                onSelectClick = { videoPicker.launch("video/*") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 转换按钮或进度
            when {
                uiState.isConverting -> {
                    ConversionProgressCard(
                        step = uiState.conversionStep,
                        progress = uiState.progress
                    )
                }
                uiState.isSuccess -> {
                    SuccessCard(
                        onReset = { viewModel.reset() }
                    )
                }
                else -> {
                    ConvertButton(
                        enabled = uiState.selectedVideoUri != null,
                        onClick = { viewModel.startConversion() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 说明文字
            Text(
                text = "生成的动态照片将保存到相册",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
    
    // 错误提示
    uiState.errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("出错了") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
fun VideoSelectorCard(
    hasVideo: Boolean,
    videoInfo: com.example.videotolivephoto.data.VideoInfo?,
    onSelectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasVideo) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onSelectClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (hasVideo && videoInfo != null) {
                // 已选择视频
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoFile,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "已选择视频",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "时长: ${videoInfo.durationText}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "分辨率: ${videoInfo.resolutionText}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // 未选择视频
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "点击选择视频",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "支持 MP4 格式",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ConvertButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Transform,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "开始转换",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ConversionProgressCard(
    step: ConversionStep,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = when (step) {
                    ConversionStep.EXTRACTING_FRAME -> "正在提取首帧..."
                    ConversionStep.MERGING -> "正在合并文件..."
                    ConversionStep.WRITING_METADATA -> "正在写入元数据..."
                    ConversionStep.SAVING -> "正在保存到相册..."
                    else -> "处理中..."
                },
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SuccessCard(
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "转换成功！",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "动态照片已保存到相册",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onReset,
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("继续转换")
            }
        }
    }
}
