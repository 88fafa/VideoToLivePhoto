package com.example.videotolivephoto.ui

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.videotolivephoto.data.LivePhotoGenerator
import com.example.videotolivephoto.data.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 主界面 ViewModel
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val generator = LivePhotoGenerator(application)
    
    // UI 状态
    var uiState by mutableStateOf(UiState())
        private set
    
    /**
     * 选择视频
     */
    fun selectVideo(uri: Uri) {
        viewModelScope.launch {
            val info = withContext(Dispatchers.IO) {
                generator.getVideoInfo(uri)
            }
            
            uiState = uiState.copy(
                selectedVideoUri = uri,
                videoInfo = info,
                errorMessage = null
            )
        }
    }
    
    /**
     * 开始转换
     */
    fun startConversion() {
        val uri = uiState.selectedVideoUri ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(
                isConverting = true,
                conversionStep = ConversionStep.EXTRACTING_FRAME,
                progress = 0.1f
            )
            
            try {
                // 1. 提取首帧
                uiState = uiState.copy(progress = 0.2f)
                
                // 2. 创建输出文件
                uiState = uiState.copy(
                    conversionStep = ConversionStep.MERGING,
                    progress = 0.4f
                )
                
                val outputFile = createOutputFile()
                    ?: throw IllegalStateException("无法创建输出文件")
                
                // 3. 生成动态照片
                uiState = uiState.copy(
                    conversionStep = ConversionStep.WRITING_METADATA,
                    progress = 0.7f
                )
                
                val result = withContext(Dispatchers.IO) {
                    generator.generateLivePhoto(uri, outputFile)
                }
                
                result.fold(
                    onSuccess = { file ->
                        // 4. 保存到相册
                        uiState = uiState.copy(progress = 0.9f)
                        saveToGallery(file)
                        
                        uiState = uiState.copy(
                            isConverting = false,
                            isSuccess = true,
                            outputFile = file,
                            progress = 1.0f
                        )
                    },
                    onFailure = { error ->
                        uiState = uiState.copy(
                            isConverting = false,
                            errorMessage = error.message ?: "转换失败"
                        )
                    }
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isConverting = false,
                    errorMessage = e.message ?: "转换失败"
                )
            }
        }
    }
    
    /**
     * 创建输出文件
     */
    private fun createOutputFile(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "MVIMG_$timestamp.jpg"
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 使用应用私有目录
            File(getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        } else {
            // Android 9 及以下使用公共目录
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera/$fileName").apply {
                parentFile?.mkdirs()
            }
        }
    }
    
    /**
     * 保存到系统相册
     * 适配 Android 10+ Scoped Storage 和 Android 14+ 新特性
     */
    private fun saveToGallery(file: File) {
        when {
            // Android 14+ (API 34+)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                saveUsingMediaStore(file)
            }
            // Android 10-13 (API 29-33)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                saveUsingMediaStore(file)
            }
            // Android 9 及以下，文件已在 DCIM/Camera 目录
            else -> {
                // 发送广播通知系统扫描新文件
                val intent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                    data = android.net.Uri.fromFile(file)
                }
                getApplication<Application>().sendBroadcast(intent)
            }
        }
    }
    
    /**
     * 使用 MediaStore 保存到相册
     */
    private fun saveUsingMediaStore(file: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Camera")
            // IS_PENDING 在 Android 14+ 已弃用，但仍可向后兼容
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        
        val resolver = getApplication<Application>().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let { imageUri ->
            resolver.openOutputStream(imageUri)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
            
            // 更新文件状态为可访问
            contentValues.clear()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
        }
    }
    
    /**
     * 重置状态
     */
    fun reset() {
        uiState = UiState()
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

/**
 * UI 状态数据类
 */
data class UiState(
    val selectedVideoUri: Uri? = null,
    val videoInfo: VideoInfo? = null,
    val isConverting: Boolean = false,
    val conversionStep: ConversionStep = ConversionStep.IDLE,
    val progress: Float = 0f,
    val isSuccess: Boolean = false,
    val outputFile: File? = null,
    val errorMessage: String? = null
)

/**
 * 转换步骤
 */
enum class ConversionStep {
    IDLE,
    EXTRACTING_FRAME,
    MERGING,
    WRITING_METADATA,
    SAVING
}
