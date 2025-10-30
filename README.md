# CareSelfDemo - 好好爱自己温馨提示应用

这是一个基于Ktor框架开发的Web应用，启动后会在浏览器中显示全屏随机弹出的温馨提示，每个弹窗会在5秒后自动消失，并且无限循环显示。

## 功能特性

- 🌼 自动全屏显示随机颜色和位置的温馨提示弹窗
- ⏱️ 每个弹窗5秒后自动淡出消失
- 🔄 无限循环持续显示新的提示
- 🌐 支持通过命令行参数指定服务器端口
- 📡 启动时自动显示服务器的访问地址（localhost和本地网络IP）

## 系统要求

- JDK 11 或更高版本
- 支持现代HTML5的浏览器（Chrome、Firefox、Edge等）

## 安装与使用

### 方法一：直接运行JAR文件

1. 确保已安装JDK 11或更高版本
2. 进入`build/libs`目录
3. 运行以下命令启动应用：
   
   ```bash
   java -jar CareSelf.jar
   ```
   
   或者指定端口启动：
   
   ```bash
   java -jar CareSelf.jar --server.port=8080
   ```

### 方法二：从源码构建

1. 克隆或下载本项目代码
2. 进入项目根目录
3. 运行以下命令构建项目：
   
   ```bash
   # Windows系统
   gradlew.bat build
   
   # Linux/Mac系统
   ./gradlew build
   ```
4. 构建完成后，JAR文件将生成在`build/libs`目录下
5. 按照方法一的步骤运行JAR文件

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
java -jar CareSelf.jar --server.port=8080
```

默认端口为7789。

### 修改提示内容

如需修改或添加提示内容，请编辑源代码中的`TIPS`列表。修改后需要重新构建项目。

## 项目结构

```
CareSelfDemo/
├── src/
│   └── main/
│       └── kotlin/
│           └── kim/hhhhhy/mock/care/
│               └── Main.kt    # 主程序文件
├── build.gradle.kts           # Gradle构建配置
├── gradlew                    # Linux/Mac Gradle包装器
├── gradlew.bat                # Windows Gradle包装器
└── README.md                  # 本文档
```

## 技术栈

- Kotlin 语言
- Ktor Web框架
- Netty 服务器
- HTML/CSS/JavaScript 前端

## 注意事项

1. 弹窗会持续显示，如果需要停止，请关闭浏览器标签页
2. 本应用仅在本地网络内可访问，如需公网访问，请配置端口转发或使用ngrok等工具
3. 过多的弹窗可能会影响浏览器性能，请根据实际情况调整弹窗频率

## 许可证

本项目仅供学习和个人使用。

## 开发说明

### 重新构建项目

修改代码后，运行以下命令重新构建：

```bash
# 清理旧的构建文件
./gradlew clean

# 构建新项目
./gradlew build
```

构建完成后，新的JAR文件会覆盖旧版本。