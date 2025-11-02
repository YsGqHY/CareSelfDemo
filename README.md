# CareSelfDemo - 好好爱自己温馨提示应用

这是一个基于Ktor框架开发的Web应用，启动后会在浏览器中显示全屏随机弹出的温馨提示，每个弹窗会在5秒后自动消失，并且无限循环显示。支持实时背景图片监控和自定义功能。

## 功能特性

- 🌼 自动全屏显示随机颜色和位置的温馨提示弹窗
- ⏱️ 每个弹窗5秒后自动淡出消失
- 🔄 无限循环持续显示新的提示
- 🌐 支持通过命令行参数指定服务器端口
- 📡 启动时自动显示服务器的访问地址（localhost和本地网络IP）
- 🖼️ 实时监控背景图片目录，自动更新页面和弹窗背景
- 📁 支持动态添加、修改和删除背景图片文件
- 🛡️ 自动检测并过滤有效的图片格式文件

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
   java -jar build/libs/CareSelfDemo-1.1.0.jar
   ```

4. 指定端口运行
   
   ```bash
   # 通过Gradle运行时指定端口
   # Windows
   gradlew.bat run --args="--server.port=8080"
   
   # Linux/Mac
   ./gradlew run --args="--server.port=8080"
   
   # 或者通过JAR文件指定端口
   java -jar build/libs/CareSelfDemo-1.1.0.jar --server.port=8080
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

## 自定义配置

### 修改端口号

可以通过命令行参数`--server.port`指定服务器端口，例如：

```bash
# 通过Gradle运行时
./gradlew run --args="--server.port=8080"

# 通过构建的JAR文件运行时
java -jar build/libs/CareSelfDemo-1.1.0.jar --server.port=8080
```

默认端口为7789。

### 修改提示内容

如需修改或添加提示内容，请编辑源代码中的`TIPS`列表。修改后需要重新构建项目。

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
3. 过多的弹窗可能会影响浏览器性能，请根据实际情况调整弹窗频率
4. 确保bg和popup文件夹有读写权限，以便文件监控系统正常工作
5. 较大的图片文件可能会影响页面加载速度，建议优化图片大小

## 许可证

本项目仅供学习和个人使用。

## 开发说明

### 版本信息

当前版本：1.1.0

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

- 1.1.0：新增文件监控系统，支持实时背景图片更新
- 1.0.0：初始版本，实现基础的温馨提示弹窗功能