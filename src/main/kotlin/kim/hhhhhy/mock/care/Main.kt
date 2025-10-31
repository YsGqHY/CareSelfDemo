package kim.hhhhhy.mock.care

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * 扩展函数：将List<String>转换为JSON字符串
 */
fun List<String>.toJson(): String {
    return this.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
}

/**
 * 更新页面背景图片列表
 */
fun updatePageBackgroundImages(images: List<String>) {
    try {
        AppConfig.updatePageBackgroundImages(images)
        println("成功更新页面背景图片列表: ${images.size} 张图片")
    } catch (e: Exception) {
        println("更新页面背景图片列表时出错: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 更新弹窗背景图片列表
 */
fun updatePopupBackgroundImages(images: List<String>) {
    try {
        AppConfig.updatePopupBackgroundImages(images)
        println("成功更新弹窗背景图片列表: ${images.size} 张图片")
    } catch (e: Exception) {
        println("更新弹窗背景图片列表时出错: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 应用入口点
 */
fun main(args: Array<String>) {
    println("正在启动应用...")

    // 解析命令行参数获取端口号
    val port = NetworkUtils.parsePort(args)

    // 使用CountDownLatch等待初始图片加载完成（但设置超时以避免长时间阻塞）
    val initialLoadLatch = CountDownLatch(2) // 等待两个目录的初始加载

    // 获取文件监控器实例
    val fileWatcher = FileWatcherSingleton.getInstance()

    // 开始监控bg目录（页面背景）
    fileWatcher.startWatching("bg") { images ->
        println("页面背景图片列表已更新: ${images.size} 张图片")
        updatePageBackgroundImages(images)
        initialLoadLatch.countDown()
    }

    // 开始监控popup目录（弹窗背景）
    fileWatcher.startWatching("popup") { images ->
        println("弹窗背景图片列表已更新: ${images.size} 张图片")
        updatePopupBackgroundImages(images)
        initialLoadLatch.countDown()
    }

    // 等待初始图片加载，但设置超时以避免长时间阻塞
    try {
        initialLoadLatch.await(2, TimeUnit.SECONDS)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        println("初始图片加载等待被中断")
    }

    // 打印服务器地址信息
    NetworkUtils.printServerAddresses(port)

    // 确保程序退出时停止文件监控
    Runtime.getRuntime().addShutdownHook(Thread {
        println("正在关闭资源...")
        // 停止所有文件监控
        fileWatcher.stopAll()
        println("资源已关闭")
    })

    // 启动服务器
    val server = CareServer()
    try {
        server.start(port)
    } catch (e: Exception) {
        println("服务器启动失败: ${e.message}")
        e.printStackTrace()
    } finally {
        // 确保在服务器停止时也停止监控
        fileWatcher.stopAll()
    }
}

///**
// * 更新页面背景图片配置
// * @param imagePaths 加载的图片路径列表
// */
//private fun updatePageBackgroundImages(imagePaths: List<String>) {
//    println("成功加载 ${imagePaths.size} 张页面背景图片，正在更新配置...")
//
//    try {
//        // 使用AppConfig提供的公共方法更新页面背景图片列表
//        AppConfig.updatePageBackgroundImages(imagePaths)
//        println("页面背景图片配置更新成功")
//    } catch (e: Exception) {
//        println("更新页面背景图片配置时出错: ${e.message}")
//        e.printStackTrace()
//    }
//}
//
///**
// * 更新弹窗背景图片配置
// * @param imagePaths 加载的图片路径列表
// */
//private fun updatePopupBackgroundImages(imagePaths: List<String>) {
//    println("成功加载 ${imagePaths.size} 张弹窗背景图片，正在更新配置...")
//
//    try {
//        // 使用AppConfig提供的公共方法更新弹窗背景图片列表
//        AppConfig.updatePopupBackgroundImages(imagePaths)
//        println("弹窗背景图片配置更新成功")
//    } catch (e: Exception) {
//        println("更新弹窗背景图片配置时出错: ${e.message}")
//        e.printStackTrace()
//    }
//}