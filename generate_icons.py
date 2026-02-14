"""
生成应用图标脚本
需要安装 Pillow: pip install Pillow
"""
from PIL import Image, ImageDraw
import os

def create_app_icon(size):
    """创建指定尺寸的应用图标"""
    img = Image.new('RGBA', (size, size), (21, 101, 192, 255))  # #1565C0 蓝色背景
    draw = ImageDraw.Draw(img)
    
    # 计算缩放比例
    scale = size / 512
    padding = int(80 * scale)
    
    # 照片外框（白色）
    frame_rect = [
        padding,
        padding,
        size - padding,
        int(size - padding * 0.8)
    ]
    draw.rectangle(frame_rect, fill=(255, 255, 255, 255))
    
    # 照片内部（浅蓝色）
    inner_padding = padding + int(20 * scale)
    inner_rect = [
        inner_padding,
        int(padding + 30 * scale),
        size - inner_padding,
        int(size - padding * 0.8 - 20 * scale)
    ]
    draw.rectangle(inner_rect, fill=(227, 242, 253, 255))  # #E3F2FD
    
    # 播放按钮圆圈（蓝色）
    center_x = size // 2
    center_y = size // 2 - int(10 * scale)
    radius = int(80 * scale)
    draw.ellipse([
        center_x - radius,
        center_y - radius,
        center_x + radius,
        center_y + radius
    ], fill=(33, 150, 243, 255))  # #2196F3
    
    # 播放三角形（白色）
    triangle_size = int(40 * scale)
    triangle_points = [
        (center_x - triangle_size//3, center_y - triangle_size//2),
        (center_x - triangle_size//3, center_y + triangle_size//2),
        (center_x + triangle_size//2, center_y)
    ]
    draw.polygon(triangle_points, fill=(255, 255, 255, 255))
    
    # 动态效果弧线（蓝色）
    arc_y = int(size - padding * 1.2)
    arc_rect = [
        (int(padding * 1.5), arc_y - int(20 * scale)),
        (size - int(padding * 1.5), arc_y + int(20 * scale))
    ]
    draw.arc(arc_rect, start=0, end=180, fill=(33, 150, 243, 255), width=int(6 * scale))
    
    return img

def generate_all_icons():
    """生成所有尺寸的图标"""
    # Android 图标尺寸
    sizes = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192,
        'playstore': 512
    }
    
    base_dir = "app/src/main/res"
    
    for folder, size in sizes.items():
        if folder == 'playstore':
            # Play Store 图标放在项目根目录
            output_path = f"app_icon_{size}.png"
        else:
            # Android 资源目录
            folder_path = f"{base_dir}/{folder}"
            os.makedirs(folder_path, exist_ok=True)
            output_path = f"{folder_path}/ic_launcher.png"
            output_path_round = f"{folder_path}/ic_launcher_round.png"
        
        # 生成方形图标
        icon = create_app_icon(size)
        icon.save(output_path, 'PNG')
        print(f"Generated: {output_path}")
        
        # 生成圆形图标（如果不是 playstore）
        if folder != 'playstore':
            # 创建圆形遮罩
            mask = Image.new('L', (size, size), 0)
            mask_draw = ImageDraw.Draw(mask)
            mask_draw.ellipse([0, 0, size, size], fill=255)
            
            round_icon = Image.new('RGBA', (size, size), (0, 0, 0, 0))
            round_icon.paste(icon, (0, 0))
            round_icon.putalpha(mask)
            round_icon.save(output_path_round, 'PNG')
            print(f"Generated: {output_path_round}")
    
    print("\n✅ 所有图标生成完成！")
    print("📝 提示：如果你不满意这个简单图标，可以：")
    print("   1. 使用 Android Studio 的 Image Asset Studio 重新生成")
    print("   2. 使用 ModelScope AI 生成精美图标")
    print("   3. 手动设计并替换 PNG 文件")

if __name__ == "__main__":
    generate_all_icons()
