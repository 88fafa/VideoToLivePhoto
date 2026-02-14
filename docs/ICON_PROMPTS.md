# ModelScope 图标生成提示词

## 应用主题
视频转动态照片工具 - 将普通视频转换为 Android 动态照片（Live Photo/Motion Photo）

## 推荐提示词（中文）

### 方案 1：极简扁平风格（推荐）
```
极简风格移动应用图标，蓝色渐变圆形背景，
中央是白色相机快门图标，中间有蓝色播放三角形，
扁平化设计，Material Design 3 风格，
纯色背景，无阴影，高清矢量风格，
512x512像素，1:1比例
```

### 方案 2：现代渐变风格
```
现代应用图标，深蓝色到浅蓝色渐变背景，
中央有白色照片轮廓，照片上有播放按钮叠加，
玻璃拟态效果，圆角矩形，
简洁几何图形，iOS 和 Android 混合风格，
专业设计感，4K高清
```

### 方案 3：拟物风格
```
拟物化应用图标，照片卡片悬浮效果，
照片显示风景缩略图，右下角有播放按钮徽章，
微妙阴影和光泽，iOS 6 风格，
精致细节，高清渲染，白色背景
```

### 方案 4：抽象艺术风格
```
抽象艺术应用图标，蓝色和青色渐变，
视频播放符号与相机快门融合的几何图形，
流畅曲线，现代极简主义，
应用商店展示图风格，专业UI设计
```

## 推荐提示词（English）

### Option 1: Minimalist Flat Design (Recommended)
```
Minimalist mobile app icon, blue gradient circular background,
white camera shutter icon with blue play triangle in center,
flat design, Material Design 3 style,
solid colors, no shadows, high-quality vector style,
512x512 pixels, 1:1 aspect ratio
```

### Option 2: Modern Gradient Style
```
Modern app icon, dark blue to light blue gradient background,
white photo frame with play button overlay in center,
glassmorphism effect, rounded rectangle,
clean geometric shapes, iOS and Android hybrid style,
professional design, 4K high definition
```

## 使用说明

### 1. 获取 ModelScope API Key
1. 访问 https://www.modelscope.cn
2. 注册/登录账号
3. 进入控制台 → API Key 管理
4. 创建新的 API Key

### 2. 配置
创建 `modelscope_config.json`：
```json
{
  "api_key": "ms-xxxxx-xxxxx-xxxxx",
  "default_aspect_ratio": "1:1",
  "generate_model": "Tongyi-MAI/Z-Image-Turbo"
}
```

### 3. 生成图标
```python
# 使用上面的提示词生成
# 输出尺寸建议 512x512px（用于 Play Store）
# 然后在 Android Studio 中使用 Image Asset Studio 生成其他尺寸
```

### 4. 导入到 Android 项目
1. 在 Android Studio 中右键 `res` 文件夹
2. New → Image Asset
3. Asset Type: Image
4. Path: 选择生成的 512x512 PNG
5. 调整缩放和裁剪
6. Next → Finish

## 当前图标说明

项目已包含简单矢量图标：
- **位置**：`res/drawable/ic_launcher_foreground.xml`
- **风格**：Material Design 扁平风格
- **颜色**：蓝色主题 (#2196F3)
- **元素**：照片外框 + 播放按钮 + 动态弧线

**如果不满意当前图标，可以使用上述提示词生成更精美的版本！**

## 图标尺寸参考

生成 512x512px 后，Android Studio 会自动生成：
- mdpi: 48x48px
- hdpi: 72x72px
- xhdpi: 96x96px
- xxhdpi: 144x144px
- xxxhdpi: 192x192px

Play Store 需要：512x512px
