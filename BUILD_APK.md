# 构建 APK 说明

## 方法 1: 使用 Android Studio（推荐）

### 步骤

1. **安装 Android Studio**
   - 下载地址：https://developer.android.com/studio
   - 建议版本：Hedgehog (2023.1.1) 或更新版本

2. **打开项目**
   ```
   File → Open → 选择 VideoToLivePhoto 文件夹
   ```

3. **同步 Gradle**
   - 点击 "Sync Project with Gradle Files" 按钮
   - 等待同步完成

4. **构建 Debug APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```
   
5. **获取 APK**
   - 构建完成后，点击右下角的通知 "locate"
   - 或路径：`app/build/outputs/apk/debug/app-debug.apk`

6. **构建 Release APK**
   ```
   Build → Generate Signed Bundle / APK → APK
   - 创建或选择密钥库（Keystore）
   - 选择 release 版本
   - 构建完成后在 `app/build/outputs/apk/release/` 目录
   ```

## 方法 2: 使用命令行

### 前提条件
- 安装 Android SDK
- 配置环境变量 `ANDROID_HOME`
- 安装 JDK 17+

### 构建命令

```bash
# 进入项目目录
cd VideoToLivePhoto

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需要签名配置）
./gradlew assembleRelease
```

### 输出路径
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 方法 3: 使用 GitHub Actions 自动构建

项目已配置 `.github/workflows/build.yml`，可以自动构建 APK：

1. 将代码推送到 GitHub
2. 在 GitHub 页面点击 "Actions" 标签
3. 选择 "Build APK" 工作流
4. 点击 "Run workflow"
5. 构建完成后在 Artifacts 中下载 APK

## APK 安装

### 通过 ADB 安装
```bash
# 连接手机，开启 USB 调试
adb install app/build/outputs/apk/debug/app-debug.apk

# 覆盖安装
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 直接安装
1. 将 APK 文件传输到手机
2. 在文件管理器中点击 APK
3. 允许安装未知来源应用
4. 完成安装

## 常见问题

### 1. Gradle 同步失败
```
解决方案：
- File → Invalidate Caches / Restart
- 或删除 .gradle 文件夹后重新同步
```

### 2. 构建失败：SDK 版本不匹配
```
解决方案：
- 打开 SDK Manager
- 安装 Android 15 (API 35)
- 安装 Android SDK Build-Tools
```

### 3. JDK 版本问题
```
项目需要 JDK 17+
File → Settings → Build → Build Tools → Gradle
设置 Gradle JDK 为 17 或更高版本
```

## 图标说明

项目已配置矢量图标（Vector Drawable）：
- 前景：`res/drawable/ic_launcher_foreground.xml`
- 背景：`res/drawable/ic_launcher_background.xml`

**如需更换图标：**
1. 右键 `res` 文件夹
2. New → Image Asset
3. 选择图标类型（Launcher Icons）
4. 导入图片或配置矢量图标
5. 点击 Next → Finish

## 签名 Release APK

### 创建密钥库
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
```

### 配置签名（可选）
在 `app/build.gradle` 中添加：
```gradle
android {
    signingConfigs {
        release {
            storeFile file("my-release-key.jks")
            storePassword "password"
            keyAlias "my-alias"
            keyPassword "password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

**注意**：不要将密钥库密码提交到 Git！
