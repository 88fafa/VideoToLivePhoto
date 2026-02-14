package com.example.videotolivephoto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.videotolivephoto.ui.MainScreen
import com.example.videotolivephoto.ui.theme.VideoToLivePhotoTheme

class MainActivity : ComponentActivity() {
    
    companion object {
        private const val REQUEST_PERMISSIONS = 100
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Android 15+ 强制 Edge-to-Edge，使用 enableEdgeToEdge() 适配
        enableEdgeToEdge()
        
        // 请求必要权限（适配 Android 14+ 新权限模型）
        requestPermissions()
        
        setContent {
            VideoToLivePhotoTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    
    private fun requestPermissions() {
        val permissions = when {
            // Android 14+ (API 34+) - 使用 VISUAL_USER_SELECTED 等更细化的权限
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Android 14+ 可以使用新的照片选择器，不需要存储权限
                // 但这里我们保留传统权限以保持兼容性
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            // Android 13 (API 33)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            // Android 10-12 (API 29-32)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            // Android 9 及以下 (API 28-)
            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, REQUEST_PERMISSIONS)
        }
    }
}
