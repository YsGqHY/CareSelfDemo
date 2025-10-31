package kim.hhhhhy.mock.care


/**
 * 扩展函数：将List<String>转换为JSON字符串
 */
fun List<String>.toJson(): String {
    return this.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
}

/**
 * 应用入口点
 */
fun main(args: Array<String>) {
    // 解析命令行参数获取端口号
    val port = NetworkUtils.parsePort(args)

    // 打印服务器地址信息
    NetworkUtils.printServerAddresses(port)

    // 启动服务器
    val server = CareServer()
    server.start(port)
}