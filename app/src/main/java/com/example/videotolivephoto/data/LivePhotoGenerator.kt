package com.example.videotolivephoto.data

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import java.io.*
import java.util.*

/**
 * 动态照片生成器
 * 将视频转换为动态照片（Motion Photo格式）
 */
class LivePhotoGenerator(private val context: Context) {
    
    companion object {
        const val BUFFER_SIZE = 64 * 1024 // 64KB 缓冲区
        const val DEFAULT_TIMESTAMP_US = 500000L // 默认展示时间戳 500ms
    }
    
    /**
     * 生成动态照片
     * 
     * @param videoUri 视频文件 URI
     * @param outputFile 输出文件路径
     * @param timestampUs 视频展示时间点（微秒）
     * @return 生成的动态照片文件
     */
    fun generateLivePhoto(
        videoUri: Uri,
        outputFile: File,
        timestampUs: Long = DEFAULT_TIMESTAMP_US
    ): Result<File> {
        return try {
            // 1. 提取视频首帧
            val frameBitmap = extractFirstFrame(videoUri, timestampUs)
                ?: return Result.failure(IllegalStateException("无法提取视频帧"))
            
            // 2. 保存首帧为临时 JPEG 文件
            val tempImageFile = File.createTempFile("frame_", ".jpg", context.cacheDir)
            saveBitmapToFile(frameBitmap, tempImageFile)
            
            // 3. 获取视频文件
            val videoFile = getFileFromUri(videoUri)
                ?: return Result.failure(IllegalStateException("无法获取视频文件"))
            
            // 4. 创建动态照片文件
            createLivePhotoFile(tempImageFile, videoFile, outputFile, timestampUs)
            
            // 5. 清理临时文件
            tempImageFile.delete()
            
            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 使用自定义图片生成动态照片
     */
    fun generateLivePhotoWithImage(
        videoUri: Uri,
        imageUri: Uri,
        outputFile: File,
        timestampUs: Long = DEFAULT_TIMESTAMP_US
    ): Result<File> {
        return try {
            val videoFile = getFileFromUri(videoUri)
                ?: return Result.failure(IllegalStateException("无法获取视频文件"))
            
            val imageFile = getFileFromUri(imageUri)
                ?: return Result.failure(IllegalStateException("无法获取图片文件"))
            
            createLivePhotoFile(imageFile, videoFile, outputFile, timestampUs)
            
            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 提取视频首帧
     */
    private fun extractFirstFrame(videoUri: Uri, timeUs: Long = 0): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)
            retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
    
    /**
     * 保存 Bitmap 到文件
     */
    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
    }
    
    /**
     * 创建动态照片文件
     * 核心逻辑：图片 + 视频拼接 + XMP 元数据
     */
    private fun createLivePhotoFile(
        imageFile: File,
        videoFile: File,
        outputFile: File,
        timestampUs: Long
    ) {
        // 1. 复制图片文件到输出
        imageFile.copyTo(outputFile, overwrite = true)
        
        // 2. 追加视频文件到输出文件末尾
        val videoSize = appendVideoToImage(outputFile, videoFile)
        
        // 3. 写入 XMP 元数据
        writeXmpMetadata(outputFile, videoSize, timestampUs)
    }
    
    /**
     * 追加视频到图片文件
     */
    private fun appendVideoToImage(imageFile: File, videoFile: File): Long {
        val videoSize = videoFile.length()
        
        FileOutputStream(imageFile, true).use { output ->
            FileInputStream(videoFile).use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                
                while (input.read(buffer).also { bytesRead = it } > 0) {
                    output.write(buffer, 0, bytesRead)
                }
            }
        }
        
        return videoSize
    }
    
    /**
     * 写入 XMP 元数据
     * 支持 MotionPhoto 和 MicroVideo 两种格式
     */
    private fun writeXmpMetadata(
        file: File,
        videoSize: Long,
        timestampUs: Long,
        useMicroVideo: Boolean = false
    ) {
        val exif = ExifInterface(file.absolutePath)
        
        // 构建 XMP 元数据
        val xmpXml = if (useMicroVideo) {
            buildMicroVideoXmp(videoSize, timestampUs)
        } else {
            buildMotionPhotoXmp(videoSize, timestampUs)
        }
        
        // 写入 XMP 元数据
        exif.setAttribute(ExifInterface.TAG_XMP, xmpXml)
        exif.saveAttributes()
    }
    
    /**
     * 构建 MotionPhoto XMP（Google/华为/荣耀标准）
     */
    private fun buildMotionPhotoXmp(videoSize: Long, timestampUs: Long): String {
        return """<?xpacket begin="" id="W5M0MpCehiHzreSzNTczkc9d"?>
<x:xmpmeta xmlns:x="adobe:ns:meta/" x:xmptk="Adobe XMP Core 5.1.0">
  <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdf:Description rdf:about=""
      xmlns:GCamera="http://ns.google.com/photos/1.0/camera/"
      GCamera:MotionPhoto="1"
      GCamera:MotionPhotoVersion="1"
      GCamera:MotionPhotoPresentationTimestampUs="$timestampUs"
      xmlns:Container="http://ns.google.com/photos/1.0/container/"
      xmlns:Item="http://ns.google.com/photos/1.0/container/item/">
      <Container:Directory>
        <rdf:Seq>
          <rdf:li rdf:parseType="Resource">
            <Item:Mime>image/jpeg</Item:Mime>
            <Item:Semantic>Primary</Item:Semantic>
          </rdf:li>
          <rdf:li rdf:parseType="Resource">
            <Item:Mime>video/mp4</Item:Mime>
            <Item:Semantic>MotionPhoto</Item:Semantic>
            <Item:Length>$videoSize</Item:Length>
          </rdf:li>
        </rdf:Seq>
      </Container:Directory>
    </rdf:Description>
  </rdf:RDF>
</x:xmpmeta>
<?xpacket end="w"?>""".trimIndent()
    }
    
    /**
     * 构建 MicroVideo XMP（小米标准）
     */
    private fun buildMicroVideoXmp(videoSize: Long, timestampUs: Long): String {
        return """<?xpacket begin="" id="W5M0MpCehiHzreSzNTczkc9d"?>
<x:xmpmeta xmlns:x="adobe:ns:meta/" x:xmptk="Adobe XMP Core 5.1.0">
  <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdf:Description rdf:about=""
      xmlns:GCamera="http://ns.google.com/photos/1.0/camera/"
      GCamera:MicroVideo="1"
      GCamera:MicroVideoVersion="1"
      GCamera:MicroVideoOffset="$videoSize"
      GCamera:MicroVideoPresentationTimestampUs="$timestampUs"/>
  </rdf:RDF>
</x:xmpmeta>
<?xpacket end="w"?>""".trimIndent()
    }
    
    /**
     * 从 URI 获取文件
     */
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val path = cursor.getString(0)
                    File(path).takeIf { it.exists() }
                } else null
            }
        } catch (e: Exception) {
            // 如果无法直接获取路径，复制到临时文件
            copyUriToTempFile(uri)
        }
    }
    
    /**
     * 将 URI 内容复制到临时文件
     */
    private fun copyUriToTempFile(uri: Uri): File? {
        return try {
            val tempFile = File.createTempFile("temp_", ".tmp", context.cacheDir)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 获取视频信息
     */
    fun getVideoInfo(videoUri: Uri): VideoInfo? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)
            
            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0
            
            val width = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
            )?.toIntOrNull() ?: 0
            
            val height = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
            )?.toIntOrNull() ?: 0
            
            VideoInfo(duration, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}

/**
 * 视频信息数据类
 */
data class VideoInfo(
    val durationMs: Long,
    val width: Int,
    val height: Int
) {
    val durationText: String
        get() {
            val seconds = durationMs / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%02d:%02d.%02d", minutes, remainingSeconds, (durationMs % 1000) / 10)
        }
    
    val resolutionText: String
        get() = "${width}x${height}"
}
