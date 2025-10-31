package kim.hhhhhy.mock.care

import java.net.NetworkInterface

/**
 * 网络工具类，提供网络相关功能
 */
object NetworkUtils {
    /**
     * 获取本机公网IP地址列表（IPv4）
     */
    fun getPublicIps(): List<String> {
        val publicIps = mutableListOf<String>()
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                
                // 跳过回环接口和禁用的接口
                if (networkInterface.isLoopback || !networkInterface.isUp) continue
                
                val inetAddresses = networkInterface.inetAddresses
                
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    
                    // 只获取IPv4地址
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
    
    /**
     * 解析命令行参数，获取端口号
     */
    fun parsePort(args: Array<String>): Int {
        args.forEach { arg ->
            if (arg.startsWith("--server.port=")) {
                try {
                    return arg.substringAfter("--server.port=").toInt().also {
                        println("使用指定端口: $it")
                    }
                } catch (e: NumberFormatException) {
                    println("端口号格式错误，使用默认端口: ${AppConfig.DEFAULT_PORT}")
                }
            }
        }
        println("使用默认端口: ${AppConfig.DEFAULT_PORT}")
        return AppConfig.DEFAULT_PORT
    }
    
    /**
     * 打印服务器地址信息
     */
    fun printServerAddresses(port: Int) {
        val localhost = "localhost:$port"
        val publicIps = getPublicIps()
        
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
    }
}