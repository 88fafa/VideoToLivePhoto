#!/bin/bash
# 推送到 GitHub 并触发自动构建

echo "📦 视频转动态照片 - GitHub 构建脚本"
echo "=================================="
echo ""

# 检查是否配置了 git
git config --global user.email "your-email@example.com"
git config --global user.name "Your Name"

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: Video to Live Photo MVP"

echo ""
echo "✅ 代码已提交到本地仓库"
echo ""
echo "下一步："
echo "1. 在 GitHub 创建新仓库（不要初始化 README）"
echo "2. 运行以下命令推送到 GitHub："
echo ""
echo "   git remote add origin https://github.com/YOUR_USERNAME/VideoToLivePhoto.git"
echo "   git branch -M main"
echo "   git push -u origin main"
echo ""
echo "3. 推送后，GitHub Actions 会自动构建 APK"
echo "4. 进入 GitHub 仓库 → Actions 标签查看构建状态"
echo "5. 构建完成后，在 Actions 页面下载 APK"
echo ""
