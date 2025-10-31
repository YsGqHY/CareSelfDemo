package kim.hhhhhy.mock.care

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.regex.Pattern

/**
 * 图片加载工具类，负责加载本地图片资源
 */
object ImageLoader {
    // 支持的图片格式
    private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    private val IMAGE_PATTERN = Pattern.compile(".*\\.(" + IMAGE_EXTENSIONS.joinToString("|", transform = String::lowercase) + ")$", Pattern.CASE_INSENSITIVE)
    
    // 线程池用于异步加载
    private val executorService = Executors.newSingleThreadExecutor {
        Thread(it, "ImageLoader")
    }
    
    /**
     * 异步加载指定目录下的所有图片
     * @param directoryPath 目录路径
     * @param callback 加载完成回调函数
     * @return Future对象，可用于取消加载
     */
    fun loadImagesAsync(directoryPath: String, callback: (List<String>) -> Unit): Future<*> {
        return executorService.submit {
            val images = try {
                loadImages(directoryPath)
            } catch (e: Exception) {
                println("加载图片时发生异常: ${e.message}")
                emptyList()
            }
            callback(images)
        }
    }
    
    /**
     * 加载指定目录下的所有图片
     * @param directoryPath 目录路径
     * @return 图片文件的相对路径列表
     */
    private fun loadImages(directoryPath: String): List<String> {
        val directory = File(directoryPath)
        
        // 检查目录是否存在
        if (!directory.exists()) {
            println("警告: 目录不存在 - $directoryPath")
            return emptyList()
        }
        
        if (!directory.isDirectory) {
            println("警告: 指定路径不是目录 - $directoryPath")
            return emptyList()
        }
        
        val images = mutableListOf<String>()
        
        // 遍历目录中的所有文件
        directory.walkTopDown().forEach { file ->
            if (file.isFile && isImageFile(file.name)) {
                try {
                    // 获取相对路径并规范化（使用正斜杠）
                    val relativePath = "./$directoryPath/" + file.name.replace('\\', '/')
                    images.add(relativePath)
                    println("找到图片: $relativePath")
                } catch (e: Exception) {
                    println("处理文件 ${file.name} 时出错: ${e.message}")
                }
            }
        }
        
        if (images.isEmpty()) {
            println("在目录 $directoryPath 中未找到图片文件")
        } else {
            println("成功加载 ${images.size} 张图片")
        }
        
        return images
    }
    
    /**
     * 检查文件是否为支持的图片格式
     * @param fileName 文件名
     * @return 是否为图片文件
     */
    private fun isImageFile(fileName: String): Boolean {
        return IMAGE_PATTERN.matcher(fileName).matches()
    }
    
    /**
     * 关闭线程池
     */
    fun shutdown() {
        executorService.shutdown()
    }
}