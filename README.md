# 视频转动态照片 - Android 项目

基于 [live-photo-conv](https://github.com/wszqkzqk/live-photo-conv) 开源项目核心逻辑的 Android 应用。

## 功能

- 将视频转换为动态照片（Motion Photo）
- 支持 Google、华为、荣耀等主流 Android 设备
- 自动提取视频首帧作为静态图
- 保存到系统相册

## 技术栈

- Kotlin
- Jetpack Compose
- MediaMetadataRetriever（视频处理）
- ExifInterface（XMP 元数据）

## 项目结构

```
app/src/main/java/com/example/videotolivephoto/
├── data/
│   └── LivePhotoGenerator.kt      # 动态照片生成核心逻辑
├── ui/
│   ├── MainScreen.kt              # 主界面
│   ├── MainViewModel.kt           # 界面状态管理
│   └── theme/                     # 主题配置
├── MainActivity.kt                # 入口 Activity
└── ...
```

## 开发计划

### 里程碑 1: MVP（当前）
- ✅ 项目搭建
- ✅ 核心转换功能
- ✅ 基础 UI
- [ ] 真机测试

### 里程碑 2: 优化
- [ ] 小米 MicroVideo 格式支持
- [ ] 自定义首帧时间点
- [ ] 视频压缩选项

### 里程碑 3: 高级功能
- [ ] 元数据模板导入（华为/荣耀深度适配）
- [ ] 批量转换
- [ ] 分享功能

## XMP 元数据格式

### MotionPhoto（Google/华为/荣耀）
```xml
Xmp.GCamera.MotionPhoto = "1"
Xmp.GCamera.MotionPhotoVersion = "1"
Xmp.GCamera.MotionPhotoPresentationTimestampUs = "500000"
Xmp.Container.Directory[1]/Item:Mime = "image/jpeg"
Xmp.Container.Directory[2]/Item:Mime = "video/mp4"
Xmp.Container.Directory[2]/Item:Length = "{video_size}"
```

### MicroVideo（小米）
```xml
Xmp.GCamera.MicroVideo = "1"
Xmp.GCamera.MicroVideoVersion = "1"
Xmp.GCamera.MicroVideoOffset = "{video_size}"
Xmp.GCamera.MicroVideoPresentationTimestampUs = "500000"
```

## 构建说明

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

## 许可证

与上游项目一致：LGPL-2.1-or-later
