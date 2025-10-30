package kim.hhhhhy.mock.care

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.InetAddress
import java.net.NetworkInterface

// 提示信息列表
val TIPS = listOf(
    "天天开心",
    "别熬夜",
    "早点休息",
    "天冷了，多穿衣服",
    "顺顺利利",
    "保持好心情",
    "梦想成真",
    "多喝水",
    "今天过得开心吗",
    "好好爱自己",
    "记得吃水果",
    "好好吃饭",
)

fun main(args: Array<String>) {
    // 解析命令行参数，默认端口为7789
    var port = 7789
    
    args.forEach { arg ->
        if (arg.startsWith("--server.port=")) {
            try {
                port = arg.substringAfter("--server.port=").toInt()
                println("使用指定端口: $port")
            } catch (e: NumberFormatException) {
                println("使用默认端口: 7789")
            }
        }
    }
    
    // 获取本机IP地址
    val localhost = "localhost:$port"
    val publicIps = getPublicIps()
    
    // 打印服务器地址信息
    println("服务器启动成功！")
    println("Localhost地址: $localhost")
    println("公网地址:")
    if (publicIps.isEmpty()) {
        println("  - 无法获取公网IP，请检查网络连接或使用ngrok等工具进行端口转发")
    } else {
        publicIps.forEach { ip ->
            println("  - $ip:$port")
        }
    }
    println("\n请在浏览器中访问上述地址查看效果")
    
    // 创建HTTP服务器，监听指定端口
    embeddedServer(
        factory = Netty,
        port = port,
        host = "0.0.0.0"
    ) {
        routing {
            get("/") {
                call.respondText(
                    text = createHtmlPage(),
                    contentType = io.ktor.http.ContentType.Text.Html
                )
            }
        }
    }.start(wait = true)
}

// 创建HTML页面，包含弹窗功能
fun createHtmlPage(): String {
    return """
        <!DOCTYPE html>
        <html lang="zh-CN">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>好好爱自己</title>
            <style>
                body {
                    font-family: 'Microsoft YaHei', sans-serif;
                    margin: 0;
                    padding: 0;
                    background-color: #f0f0f0;
                    text-align: center;
                    padding-top: 50px;
                }
                .container {
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                    background-color: white;
                    border-radius: 10px;
                    box-shadow: 0 0 10px rgba(0,0,0,0.1);
                }
                h1 {
                    color: #333;
                }
                button {
                    background-color: #4CAF50;
                    color: white;
                    border: none;
                    padding: 15px 30px;
                    font-size: 16px;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-top: 20px;
                }
                button:hover {
                    background-color: #45a049;
                }
                .popup {
                    position: fixed;
                    padding: 20px;
                    border-radius: 5px;
                    color: white;
                    font-size: 30px;
                    font-weight: bold;
                    text-align: center;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 1000;
                    animation: fadeIn 0.3s ease-in;
                }
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
            </style>
        </head>
        <body>
            
            <script>
                // 提示信息列表
                const tips = ${TIPS.toJson()};
                
                // 生成随机位置
                function getRandomPosition() {
                    const width = window.innerWidth;
                    const height = window.innerHeight;
                    const x = Math.floor(Math.random() * (width - 300));
                    const y = Math.floor(Math.random() * (height - 140));
                    return { x, y };
                }
                
                // 显示弹窗
                function showPopups() {
                    const showNextPopup = () => {
                        const tip = tips[Math.floor(Math.random() * tips.length)];
                        const popup = document.createElement('div');
                        popup.className = 'popup';
                        popup.textContent = tip;
                        popup.style.backgroundColor = 'rgb(' + Math.floor(Math.random() * 255) + ',' + Math.floor(Math.random() * 255) + ',' + Math.floor(Math.random() * 255) + ')';
                        popup.style.width = '300px';
                        popup.style.height = '140px';
                        popup.style.transition = 'opacity 0.5s ease-out';
                        
                        const position = getRandomPosition();
                        popup.style.left = position.x + 'px';
                        popup.style.top = position.y + 'px';
                        
                        document.body.appendChild(popup);
                        
                        // 5秒后自动移除弹窗（先淡出动画）
                        setTimeout(() => {
                            popup.style.opacity = '0';
                            setTimeout(() => {
                                if (popup.parentNode) {
                                    popup.parentNode.removeChild(popup);
                                }
                            }, 500);
                        }, 5000);
                        
                        // 1秒后显示下一个弹窗（控制弹窗生成频率）
                        setTimeout(showNextPopup, 150);
                    };
                    
                    showNextPopup();
                }
                
                // 页面加载后自动执行动画
                window.onload = showPopups;
            </script>
        </body>
        </html>
    """.trimIndent()
}

// 获取公网IP地址列表
fun getPublicIps(): List<String> {
    val publicIps = mutableListOf<String>()
    try {
        // 获取所有网络接口
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            
            // 跳过回环接口和禁用的接口
            if (networkInterface.isLoopback || !networkInterface.isUp) continue
            
            // 获取该接口的所有IP地址
            val inetAddresses = networkInterface.inetAddresses
            
            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()
                
                // 只获取IPv4地址（简单判断，不是回环地址且是IPv4）
                if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(':') < 0) {
                    publicIps.add(inetAddress.hostAddress)
                }
            }
        }
    } catch (e: Exception) {
        // 捕获异常，不影响程序运行
    }
    
    return publicIps
}

// 扩展函数：将List转换为JSON字符串
fun List<String>.toJson(): String {
    return this.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
}