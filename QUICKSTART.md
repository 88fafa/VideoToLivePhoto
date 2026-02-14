# 视频转动态照片 - 快速开始

## 📱 项目概述

基于 [live-photo-conv](https://github.com/wszqkzqk/live-photo-conv) 开源项目的 Android 应用。

### 功能
- 将视频转换为动态照片（Motion Photo）
- 支持 Android 8.0 - Android 16
- 兼容 Google、华为、荣耀、小米等设备

## 🚀 快速构建 APK

### 方式 1：使用 Android Studio（最简单）

1. **下载并安装 Android Studio**
   - https://developer.android.com/studio

2. **打开项目**
   - File → Open → 选择 `VideoToLivePhoto` 文件夹

3. **等待 Gradle 同步**
   - 首次打开需要下载依赖（约 5-10 分钟）

4. **构建 APK**
   - 点击菜单：`Build → Build Bundle(s) / APK(s) → Build APK(s)`
   - 或点击工具栏的绿色锤子图标 🔨

5. **获取 APK**
   - 构建完成后点击右下角 "locate" 链接
   - 文件位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方式 2：使用 GitHub Actions（无需配置环境）

1. **Fork 本项目到 GitHub**
   - 访问 https://github.com/your-username/VideoToLivePhoto

2. **触发构建**
   - 进入项目页面 → Actions 标签
   - 选择 "Build APK" → Run workflow

3. **下载 APK**
   - 等待构建完成（约 5 分钟）
   - 在 Artifacts 中下载 `debug-apk`

### 方式 3：命令行（适合开发者）

```bash
# 前提：已安装 Android SDK 和 JDK 17
cd VideoToLivePhoto

# Linux/macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug

# 输出路径：app/build/outputs/apk/debug/app-debug.apk
```

## 📋 应用图标

项目已包含自适应图标（Adaptive Icons）：
- **设计**：蓝色背景 + 白色照片外框 + 播放按钮
- **文件**：
  - `res/drawable/ic_launcher_foreground.xml`
  - `res/drawable/ic_launcher_background.xml`

**更换图标**：
1. 在 Android Studio 中右键 `res` 文件夹
2. New → Image Asset
3. 选择图标源（Clip Art、Text、Image）
4. 调整大小和位置
5. 点击 Finish

## 🎨 使用 ModelScope 生成图标（可选）

如果你想使用 AI 生成更精美的图标：

### 1. 获取 API Key
- 访问 https://modelscope.cn
- 注册账号并创建 API Key

### 2. 配置
创建 `modelscope_config.json`：
```json
{
  "api_key": "your-api-key-here",
  "default_aspect_ratio": "1:1"
}
```

### 3. 生成图标提示词示例
```
极简风格应用图标，蓝色渐变背景，
白色相机轮廓中间有播放按钮，
扁平化设计，Material Design 风格，
高清，1:1比例
```

### 4. 使用生成的图标
- 下载生成的图片（512x512px）
- 在 Android Studio 中使用 Image Asset Studio 生成所有尺寸

## ✅ 测试检查清单

在真机上测试以下功能：

- [ ] 选择视频文件
- [ ] 显示视频信息（时长、分辨率）
- [ ] 转换进度显示
- [ ] 转换成功提示
- [ ] 相册中查看动态照片
- [ ] 相册中播放动态效果（长按或点击播放按钮）

## 🔧 支持设备

| 品牌 | 系统 | 支持状态 |
|-----|------|---------|
| Google Pixel | Android 14/15 | ✅ 最佳兼容 |
| 华为 Mate/P | 鸿蒙 4 / Android 12+ | ✅ 支持 |
| 荣耀 Magic | Android 14 | ✅ 支持 |
| 小米 13/14 | Android 14 | ✅ 支持 |
| OPPO Find | Android 13+ | ✅ 支持 |
| vivo X | Android 13+ | ✅ 支持 |
| 三星 Galaxy | Android 13+ | ✅ 支持 |

## 📊 应用信息

- **包名**：`com.example.videotolivephoto`
- **版本**：1.0.0
- **大小**：约 5-8 MB
- **权限**：存储读取（用于选择视频和保存动态照片）

## 🐛 常见问题

**Q: 构建失败，提示 "Could not find com.android.tools.build:gradle"**
A: 检查 Android Studio 版本是否过旧，建议更新到最新版

**Q: 转换后的动态照片无法播放**
A: 不同厂商格式可能略有差异，尝试在其他相册应用中查看

**Q: 应用安装后闪退**
A: 检查是否授予了存储权限，Android 6.0+ 需要动态权限申请

## 📄 许可证

LGPL-2.1-or-later（与上游项目保持一致）

## 🙏 致谢

- 核心算法基于 [live-photo-conv](https://github.com/wszqkzqk/live-photo-conv)
- Material Design 3 组件由 Google 提供
