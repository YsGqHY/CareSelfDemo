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
        try {
            return NetworkInterface.getNetworkInterfaces()
                .toList()
                .flatMap { it.inetAddresses.toList() }
                .filter { !it.isLoopbackAddress && it.hostAddress.indexOf(':') < 0 }
                .map { it.hostAddress }
        } catch (e: Exception) {
            println("获取公网IP时出错: ${e.message}")
            return emptyList()
        }
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
                    println("端口号格式错误，使用默认端口: ${AppConfig.getInstance().defaultPort}")
                }
            }
        }
        println("使用默认端口: ${AppConfig.getInstance().defaultPort}")
        return AppConfig.getInstance().defaultPort
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