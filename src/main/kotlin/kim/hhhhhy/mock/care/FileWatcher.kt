package kim.hhhhhy.mock.care

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.nio.file.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * 文件监控器，使用Kotlin协程实时监控文件夹变化
 */
class FileWatcher {
    // 支持的图片格式
    private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    private val IMAGE_PATTERN = Pattern.compile("^.*\\.(" + IMAGE_EXTENSIONS.joinToString("|", transform = String::lowercase) + ")$", Pattern.CASE_INSENSITIVE)
    
    // 协程作用域，用于管理所有监控协程
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // 用于同步更新操作的互斥锁
    private val mutex = Mutex()
    
    // 存储监控任务的Map，便于取消
    private val watchJobs = ConcurrentHashMap<String, Job>()
    
    // 存储已发现的图片文件缓存
    private val imageCache = ConcurrentHashMap<String, MutableSet<String>>()
    
    /**
     * 开始监控指定目录
     * @param directoryPath 目录路径
     * @param updateCallback 更新回调函数，当图片列表变化时调用
     */
    fun startWatching(directoryPath: String, updateCallback: (List<String>) -> Unit) {
        // 先取消该目录已存在的监控任务
        stopWatching(directoryPath)
        
        // 初始化缓存集合
        imageCache[directoryPath] = ConcurrentHashMap.newKeySet()
        
        // 启动监控协程
        val job = scope.launch {
            try {
                watchDirectory(directoryPath, updateCallback)
            } catch (e: Exception) {
                println("监控目录 $directoryPath 时出错: ${e.message}")
                e.printStackTrace()
            }
        }
        
        watchJobs[directoryPath] = job
    }
    
    /**
     * 停止监控指定目录
     * @param directoryPath 目录路径
     */
    fun stopWatching(directoryPath: String) {
        watchJobs[directoryPath]?.cancel()
        watchJobs.remove(directoryPath)
        imageCache.remove(directoryPath)
    }
    
    /**
     * 停止所有监控
     */
    fun stopAll() {
        scope.cancel()
        watchJobs.clear()
        imageCache.clear()
    }
    
    /**
     * 使用Java NIO的WatchService监控目录变化
     */
    private suspend fun watchDirectory(directoryPath: String, updateCallback: (List<String>) -> Unit) = withContext(Dispatchers.IO) {
        val directory = File(directoryPath)
        
        // 检查目录是否存在，如果不存在则创建
        if (!directory.exists()) {
            println("目录不存在，正在创建: $directoryPath")
            directory.mkdirs()
        }
        
        if (!directory.isDirectory) {
            println("错误: $directoryPath 不是一个目录")
            return@withContext
        }
        
        println("开始监控目录: $directoryPath")
        
        // 初始扫描目录并加载现有图片
        scanDirectoryAndUpdate(directoryPath, updateCallback)
        
        try {
            // 创建WatchService
            val watchService = FileSystems.getDefault().newWatchService()
            val path = Paths.get(directoryPath)
            
            // 注册监控事件: 创建、修改、删除
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE)
            
            // 监控循环
            while (isActive) {
                // 获取监控事件，带超时以检查协程是否取消
                val key = withTimeoutOrNull(500) { watchService.poll() }
                
                if (key == null) {
                    // 超时，检查协程是否取消
                    if (!isActive) break
                    continue
                }
                
                // 处理所有事件
                for (event in key.pollEvents()) {
                    val kind = event.kind()
                    
                    // 忽略OVERFLOW事件
                    if (kind == StandardWatchEventKinds.OVERFLOW) continue
                    
                    // 获取文件名
                    @Suppress("UNCHECKED_CAST")
                    val fileName = (event as WatchEvent<Path>).context().fileName.toString()
                    
                    println("检测到文件变化: $fileName, 事件类型: ${kind.name()}")
                    
                    // 检查是否为图片文件
                    if (isImageFile(fileName)) {
                        // 延迟执行以避免文件操作未完成
                        delay(100)
                        // 重新扫描整个目录以确保列表准确
                        scanDirectoryAndUpdate(directoryPath, updateCallback)
                    }
                }
                
                // 重置key以继续接收事件
                val valid = key.reset()
                if (!valid) {
                    println("监控键失效，停止监控目录: $directoryPath")
                    break
                }
            }
        } catch (e: AccessDeniedException) {
            println("访问目录 $directoryPath 被拒绝: ${e.message}")
        } catch (e: ClosedWatchServiceException) {
            println("监控服务已关闭: $directoryPath")
        } catch (e: Exception) {
            println("监控目录 $directoryPath 时发生异常: ${e.message}")
            e.printStackTrace()
            // 尝试重新启动监控
            delay(1000)
//            if (isActive) {
//                watchDirectory(directoryPath, updateCallback)
//            }
        }
    }
    
    /**
     * 扫描目录并更新图片列表
     */
    private suspend fun scanDirectoryAndUpdate(directoryPath: String, updateCallback: (List<String>) -> Unit) {
        mutex.withLock {
            val directory = File(directoryPath)
            val newImages = mutableSetOf<String>()
            
            try {
                // 遍历目录中的所有文件
                directory.walkTopDown().forEach { file ->
                    if (file.isFile && isImageFile(file.name)) {
                        try {
                            // 直接使用目录名和文件名，去掉多余的 ./
                            val relativePath = "$directoryPath/${file.name}"
                            newImages.add(relativePath)
                            println("添加图片: $relativePath")
                        } catch (e: Exception) {
                            println("处理文件 ${file.name} 时出错: ${e.message}")
                        }
                    }
                }
                
                // 获取旧的图片集合
                val oldImages = imageCache[directoryPath] ?: emptySet()
                
                // 检查是否有变化
                if (newImages != oldImages) {
                    // 更新缓存
                    imageCache[directoryPath] = newImages
                    
                    println("目录 $directoryPath 的图片列表已更新，共 ${newImages.size} 张图片")
                    
                    // 调用回调函数
                    withContext(Dispatchers.Default) {
                        updateCallback(newImages.toList())
                    }
                }
            } catch (e: AccessDeniedException) {
                println("扫描目录 $directoryPath 时访问被拒绝: ${e.message}")
            } catch (e: Exception) {
                println("扫描目录 $directoryPath 时出错: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 检查文件是否为支持的图片格式
     */
    private fun isImageFile(fileName: String): Boolean {
        return IMAGE_PATTERN.matcher(fileName).matches()
    }
    
    /**
     * 获取当前监控状态
     */
    fun getMonitoringStatus(): Map<String, Boolean> {
        return watchJobs.mapValues { it.value.isActive }
    }
}

// 单例模式的文件监控器实例
object FileWatcherSingleton {
    private val watcher = FileWatcher()
    
    fun getInstance(): FileWatcher {
        return watcher
    }
}