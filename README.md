# CareSelfDemo - 好好爱自己温馨提示应用

这是一个基于Ktor框架开发的Web应用，启动后会在浏览器中显示全屏随机弹出的温馨提示。支持两种弹窗模式：定时消失模式和常驻模式（带数量限制和FIFO清理机制）。应用支持完整的配置系统，可自定义弹窗样式、行为和内容，同时提供实时背景图片监控功能。

## 功能特性

- 🌼 自动全屏显示随机颜色和位置的温馨提示弹窗
- 🎮 **三重弹窗模式**：
  - **模式1**：弹窗显示一段时间后自动淡出消失
  - **模式2**：弹窗常驻显示，实现数量限制和FIFO清理机制
  - **模式3**：中央爱心动画，使用数学算法生成爱心形状，弹窗沿爱心路径分布并动态展示
- ⚙️ **完整的配置系统**：通过config.yml文件自定义所有参数
- 🎨 支持弹窗样式自定义（尺寸、颜色、动画效果）
- ✨ 平滑的弹窗显示和移除动画（淡入淡出+缩放效果）
- 📝 可自定义温馨提示内容列表
- 🌐 支持通过命令行参数指定服务器端口
- 📡 启动时自动显示服务器的访问地址（localhost和本地网络IP）
- 🖼️ 实时监控背景图片目录，自动更新页面和弹窗背景
- 📁 支持动态添加、修改和删除背景图片文件
- 🛡️ 自动检测并过滤有效的图片格式文件
- 🔧 支持弹窗文本相对于容器的偏移调整
- 🎭 支持自定义弹窗文本颜色和描边颜色

## 系统要求

- JDK 11 或更高版本
- 支持现代HTML5的浏览器（Chrome、Firefox、Edge等）
- Git（用于克隆仓库）

## 安装与使用

### 从源码构建和运行

1. 克隆仓库
   
   ```bash
   git clone https://github.com/yourusername/CareSelfDemo.git
   cd CareSelfDemo
   ```

2. 构建项目
   
   ```bash
   # Windows系统
   gradlew.bat build
   
   # Linux/Mac系统
   ./gradlew build
   ```

3. 运行应用
   
   ```bash
   # 直接运行（推荐）
   # Windows
   gradlew.bat run
   
   # Linux/Mac
   ./gradlew run
   ```

   或者通过构建的JAR文件运行：
   
   ```bash
   # 构建后的JAR文件在build/libs目录
   java -jar build/libs/CareSelfDemo-1.2.3.jar
   ```

4. 指定端口运行
   
   ```bash
   # 通过Gradle运行时指定端口
   # Windows
   gradlew.bat run --args="--server.port=8080"
   
   # Linux/Mac
   ./gradlew run --args="--server.port=8080"
   
   # 或者通过JAR文件指定端口
   java -jar build/libs/CareSelfDemo-1.2.3.jar --server.port=8080
   ```

## 访问应用

应用启动后，控制台会显示服务器地址信息，包括：

```
服务器启动成功！
Localhost地址: localhost:7789
公网地址:
  - 192.168.1.100:7789
  - 10.0.0.5:7789

请在浏览器中访问上述地址查看效果
```

1. 在本机浏览器中访问：`http://localhost:7789`
2. 在同一局域网内的其他设备访问：`http://[本机IP]:7789`

## 配置系统

项目使用YAML配置文件进行配置，支持丰富的自定义选项。配置文件位于`src/main/resources/config.yml`。

### 服务器配置

```yaml
server:
  defaultPort: 7789  # 默认端口号
  host: "0.0.0.0"   # 主机地址
```

### 弹窗配置

```yaml
popup:
  width: "300px"                 # 弹窗宽度
  height: "100px"                # 弹窗高度
  displayTimeMs: 10000           # 弹窗显示时间（毫秒）
  fadeOutTimeMs: 500             # 弹窗淡出动画时间（毫秒）
  intervalMs: 100                # 弹窗生成间隔（毫秒）
  fadeInAnimationDuration: "0.3s" # 弹窗淡入动画持续时间
  fadeOutAnimationDuration: "0.5s" # 弹窗淡出动画持续时间
  xOffset: 0                     # 弹窗标题X轴偏移量（像素）
  yOffset: 15                    # 弹窗标题Y轴偏移量（像素）
  popup_text_color: "white"      # 弹窗文本颜色，支持十六进制、RGB/RGBA或预定义颜色名称
  popup_text_stroke_color: "rgba(0, 0, 0, 0.9)" # 弹窗文本描边颜色，支持十六进制、RGB/RGBA或预定义颜色名称
  mode: "mode3"                  # 弹窗模式：mode1（定时消失）、mode2（常驻）、mode3（中央爱心动画）
  maxCount: 100                  # 模式2下最大同时显示的弹窗数量
  heartSize: 1500                # 模式3下爱心大小（像素）
  heartColor: "#ff3e6c"          # 模式3下爱心颜色
  heartPopupCount: 60            # 模式3下爱心绘制过程中显示的弹窗总数
  heartScale: 1                  # 模式3下爱心路径绘制尺寸大小的缩放因子
  heartOffsetX: 0                # 模式3下爱心图形在页面中的X坐标偏移
  heartOffsetY: -60              # 模式3下爱心图形在页面中的Y坐标偏移
```

### 应用配置

```yaml
app:
  title: "好好爱自己"  # 应用标题
  tips:               # 提示信息列表
    - "天天开心"
    - "别熬夜"
    # 更多提示...
```

### 配置说明

1. **弹窗模式**：
   - `mode1`：传统模式，每个弹窗显示`displayTimeMs`毫秒后自动淡出消失
   - `mode2`：常驻模式，弹窗会一直显示，直到达到`maxCount`数量限制，此时会移除最早出现的弹窗（FIFO原则）
   - `mode3`：中央爱心动画模式，使用数学算法生成爱心形状，弹窗沿爱心路径分布并动态展示

2. **偏移量配置**：
   - `xOffset`：文本相对于弹窗容器的X轴偏移，正值向右偏移
   - `yOffset`：文本相对于弹窗容器的Y轴偏移，正值向下偏移

3. **文本样式配置**：
   - `popup_text_color`：弹窗文本颜色，支持十六进制、RGB/RGBA或预定义颜色名称
   - `popup_text_stroke_color`：弹窗文本描边颜色，用于增强文本可读性

3. **模式3专用配置**：
   - `heartSize`：爱心形状的基础尺寸，影响爱心的复杂程度和分辨率
   - `heartColor`：爱心形状的颜色，使用十六进制颜色值
   - `heartPopupCount`：爱心动画中显示的弹窗总数，影响爱心的填充密度
   - `heartScale`：爱心尺寸的缩放因子，可以放大或缩小整个爱心
   - `heartOffsetX/heartOffsetY`：爱心图形在页面中的位置偏移，正值分别向右和向下移动

4. **修改配置**：
   - 编辑`src/main/resources/config.yml`文件修改配置
   - 修改后需要重启应用使配置生效

### 自定义背景图片

1. 在项目根目录创建`bg`文件夹存放页面背景图片
2. 在项目根目录创建`popup`文件夹存放弹窗背景图片
3. 支持的图片格式：JPG、JPEG、PNG、GIF、WEBP
4. 系统会自动监控这些文件夹，当添加、修改或删除图片时，会实时更新背景图片列表
5. 无需重启应用，图片变更会自动生效

## 项目结构

```
CareSelfDemo/
├── .gitattributes
├── .gitignore
├── LICENSE
├── README.md                  # 本文档
├── build.gradle.kts           # Gradle构建配置
├── gradle.properties
├── gradle/
│   └── wrapper/
├── gradlew                    # Linux/Mac Gradle包装器
├── gradlew.bat                # Windows Gradle包装器
├── settings.gradle.kts
├── src/
│   ├── main/
│   │   ├── kotlin/            # Kotlin源代码
│   │   └── resources/         # 资源文件
│   └── test/
│       ├── java/              # Java测试
│       ├── kotlin/            # Kotlin测试
│       └── resources/         # 测试资源
└── # bg/和popup/文件夹会在首次运行时自动创建
    # build/目录不会包含在GitHub仓库中，需要本地构建生成
```

## 技术栈

- Kotlin 语言
- Ktor Web框架
- Netty 服务器
- HTML/CSS/JavaScript 前端
- Kotlin协程（文件监控系统）
- Java NIO WatchService（实时文件监控）

## 注意事项

1. 弹窗会持续显示，如果需要停止，请关闭浏览器标签页
2. 本应用仅在本地网络内可访问，如需公网访问，请配置端口转发或使用ngrok等工具
3. 在`mode2`模式下，过多的弹窗可能会影响浏览器性能，建议合理设置`maxCount`值
4. 在`mode1`模式下，请根据实际情况调整弹窗显示时间和间隔，避免弹窗过多
5. 确保bg和popup文件夹有读写权限，以便文件监控系统正常工作
6. 较大的图片文件可能会影响页面加载速度，建议优化图片大小
7. 在`mode2`模式下，弹窗数量达到上限后，每添加一个新弹窗就会移除一个最旧的弹窗

## 许可证

本项目仅供学习和个人使用。

## 贡献说明

欢迎对项目进行贡献！如果您想为项目添加新功能或修复bug，请遵循以下步骤：

1. Fork 项目仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启一个 Pull Request

请确保您的代码符合项目的代码风格和质量要求。对于重大更改，请先创建一个issue讨论您想要更改的内容。

## 项目架构

项目采用了清晰的分层架构，主要包括以下几个部分：

1. **核心模块**：负责应用的基本配置和启动逻辑
2. **HTML生成器**：负责生成包含弹窗功能的HTML页面
3. **配置管理**：负责加载和解析YAML配置文件
4. **文件监控系统**：负责监控背景图片文件夹的变化
5. **Web服务器**：基于Ktor框架，提供Web页面访问

主要代码文件组织结构：

```
src/main/kotlin/
├── 核心应用类（Application.kt）
├── 配置管理（ConfigManager.kt、AppConfig.kt）
├── HTML生成器（HTMLGenerator.kt）
├── 文件监控（FileWatcher.kt）
└── 工具类（Utils.kt等）
```

## API文档

### 命令行参数

- `--server.port=[端口号]`：指定服务器端口（覆盖配置文件中的设置）

### 主要接口

- `GET /`：获取主页面，包含弹窗功能
- `GET /api/config`：获取当前应用配置（如果实现了API接口）

## 常见问题

### 问题：弹窗没有显示

**解决方法**：
1. 检查浏览器控制台是否有JavaScript错误
2. 确认配置文件中的`tips`列表不为空
3. 检查浏览器是否支持HTML5和JavaScript

### 问题：背景图片没有更新

**解决方法**：
1. 确认图片格式正确（JPG、JPEG、PNG、GIF、WEBP）
2. 检查bg/popup文件夹权限
3. 尝试刷新浏览器缓存

## 开发说明

### 版本信息

当前版本：1.3.1

### 构建和测试

修改代码后，运行以下命令重新构建：

```bash
# 清理旧的构建文件
./gradlew clean

# 构建项目
./gradlew build
```

构建完成后，新的JAR文件会覆盖旧版本。

### 功能变更日志

- 1.3.1：新增弹窗文本颜色和描边颜色配置功能，支持自定义弹窗文字样式
- 1.3.0：新增模式3（中央爱心动画），使用数学算法生成爱心形状，实现弹窗沿爱心路径动态展示；新增爱心相关配置参数
- 1.2.3：新增配置系统，支持YAML配置文件，实现双重弹窗模式
- 1.2.2：优化弹窗动画效果，添加缩放动画和阴影效果
- 1.2.1：实现弹窗文本偏移功能，支持相对于容器的精确位置调整
- 1.2.0：重构弹窗生成逻辑，分离创建、显示和移除过程
- 1.1.0：新增文件监控系统，支持实时背景图片更新
- 1.0.0：初始版本，实现基础的温馨提示弹窗功能