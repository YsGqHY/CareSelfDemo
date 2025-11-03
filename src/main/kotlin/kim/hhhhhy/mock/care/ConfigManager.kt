package kim.hhhhhy.mock.care

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger

/**
 * 配置管理器，负责YAML配置文件的加载、解析和热重载
 */
class ConfigManager {
    private val logger = Logger.getLogger(ConfigManager::class.java.name)
    private val objectMapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
    private val configFileName = "config.yml"
    private val configFile = File(configFileName)
    private var lastModifiedTime = AtomicLong(0)
    private var watchJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * 初始化配置
     * 1. 从JAR包中释放配置文件到运行目录
     * 2. 加载配置文件
     * 3. 启动配置监控
     */
    fun initialize(): Boolean {
        try {
            // 释放配置文件
            releaseConfigFileFromJar()
            
            // 加载配置
            loadConfig()
            
            // 启动配置监控
            startWatchingConfig()
            
            return true
        } catch (e: Exception) {
            logger.severe("配置初始化失败: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * 从JAR包中释放配置文件到运行目录
     */
    private fun releaseConfigFileFromJar() {
        try {
            // 如果配置文件已存在，不重复释放
            if (configFile.exists()) {
                logger.info("配置文件 $configFileName 已存在，跳过释放")
                return
            }
            
            // 从resources中读取配置文件
            val resourceStream: InputStream? = javaClass.classLoader.getResourceAsStream(configFileName)
            if (resourceStream == null) {
                logger.warning("在JAR包中未找到 $configFileName 配置文件，创建默认配置")
                createDefaultConfigFile()
                return
            }
            
            // 将配置文件写入运行目录
            resourceStream.use { input ->
                FileOutputStream(configFile).use { output ->
                    input.copyTo(output)
                }
            }
            logger.info("成功从JAR包释放配置文件到: ${configFile.absolutePath}")
        } catch (e: Exception) {
            logger.severe("从JAR包释放配置文件失败: ${e.message}")
            e.printStackTrace()
            // 创建默认配置作为备选方案
            createDefaultConfigFile()
        }
    }
    
    /**
     * 创建默认配置文件
     */
    private fun createDefaultConfigFile() {
        try {
            val defaultConfig = """
server:
  defaultPort: 7789
  host: "0.0.0.0"

popup:
  width: "300px"
  height: "140px"
  displayTimeMs: 5000
  fadeOutTimeMs: 500
  intervalMs: 150
  fadeInAnimationDuration: "0.3s"
  fadeOutAnimationDuration: "0.5s"
  xOffset: 0
  yOffset: 0
  # 弹窗模式：mode1（默认，定时消失）、mode2（常驻，数量限制）
  mode: "mode1"
  # 模式2下最大同时显示的弹窗数量
  maxCount: 10

app:
  title: "好好爱自己"
  tips:
    - "天天开心"
    - "别熬夜"
    - "早点休息"
    - "天冷了，多穿衣服"
    - "顺顺利利"
    - "保持好心情"
    - "梦想成真"
    - "多喝水"
    - "今天过得开心吗"
    - "好好爱自己"
    - "记得吃水果"
    - "好好吃饭"
"""
            
            configFile.writeText(defaultConfig)
            logger.info("成功创建默认配置文件: ${configFile.absolutePath}")
        } catch (e: Exception) {
            logger.severe("创建默认配置文件失败: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * 加载配置文件
     */
    fun loadConfig(): Boolean {
        try {
            if (!configFile.exists()) {
                logger.warning("配置文件不存在: ${configFile.absolutePath}")
                return false
            }
            
            val currentModifiedTime = configFile.lastModified()
            if (currentModifiedTime <= lastModifiedTime.get()) {
                logger.fine("配置文件未发生变化，跳过加载")
                return true
            }
            
            logger.info("开始加载配置文件: ${configFile.absolutePath}")
            
            // 读取YAML配置
            val configData = objectMapper.readTree(configFile)
            
            // 创建新的配置实例
            val newConfig = AppConfig()
            
            // 解析服务器配置
            if (configData.has("server")) {
                val serverNode = configData.get("server")
                if (serverNode.has("defaultPort")) {
                    newConfig.defaultPort = serverNode.get("defaultPort").asInt()
                }
                if (serverNode.has("host")) {
                    newConfig.host = serverNode.get("host").asText()
                }
            }
            
            // 解析弹窗配置
            if (configData.has("popup")) {
                val popupNode = configData.get("popup")
                if (popupNode.has("width")) {
                    newConfig.popupWidth = popupNode.get("width").asText()
                }
                if (popupNode.has("height")) {
                    newConfig.popupHeight = popupNode.get("height").asText()
                }
                if (popupNode.has("displayTimeMs")) {
                    newConfig.popupDisplayTimeMs = popupNode.get("displayTimeMs").asInt()
                }
                if (popupNode.has("fadeOutTimeMs")) {
                    newConfig.popupFadeOutTimeMs = popupNode.get("fadeOutTimeMs").asInt()
                }
                if (popupNode.has("intervalMs")) {
                    newConfig.popupIntervalMs = popupNode.get("intervalMs").asInt()
                }
                if (popupNode.has("fadeInAnimationDuration")) {
                    newConfig.fadeInAnimationDuration = popupNode.get("fadeInAnimationDuration").asText()
                }
                if (popupNode.has("fadeOutAnimationDuration")) {
                    newConfig.fadeOutAnimationDuration = popupNode.get("fadeOutAnimationDuration").asText()
                }
                if (popupNode.has("xOffset")) {
                    newConfig.popupTitleXOffset = popupNode.get("xOffset").asInt()
                }
                if (popupNode.has("yOffset")) {
                    newConfig.popupTitleYOffset = popupNode.get("yOffset").asInt()
                }
                if (popupNode.has("mode")) {
                    newConfig.popupMode = popupNode.get("mode").asText()
                }
                if (popupNode.has("maxCount")) {
                    newConfig.maxPopupsCount = popupNode.get("maxCount").asInt()
                }
            }
            
            // 解析应用配置
            if (configData.has("app")) {
                val appNode = configData.get("app")
                if (appNode.has("title")) {
                    newConfig.appTitle = appNode.get("title").asText()
                }
                if (appNode.has("tips")) {
                    val tipsArray = appNode.get("tips")
                    if (tipsArray.isArray) {
                        val tipsList = mutableListOf<String>()
                        for (tipNode in tipsArray) {
                            tipsList.add(tipNode.asText())
                        }
                        if (tipsList.isNotEmpty()) {
                            newConfig.tips = tipsList
                        }
                    }
                }
            }
            
            // 保留现有的背景图片列表
            newConfig.pageBackgroundImages = AppConfig.getInstance().pageBackgroundImages
            newConfig.popupBackgroundImages = AppConfig.getInstance().popupBackgroundImages
            
            // 更新全局配置实例
            AppConfig.setInstance(newConfig)
            
            // 更新最后修改时间
            lastModifiedTime.set(currentModifiedTime)
            
            logger.info("配置文件加载成功")
            return true
        } catch (e: Exception) {
            logger.severe("配置文件加载失败: ${e.message}")
            e.printStackTrace()
            // 返回false但不抛出异常，确保应用能够继续运行
            return false
        }
    }
    
    /**
     * 启动配置文件监控
     */
    private fun startWatchingConfig() {
        // 取消之前的监控任务
        stopWatchingConfig()
        
        // 启动新的监控任务
        watchJob = scope.launch(Dispatchers.IO) {
            try {
                val path = configFile.toPath().parent ?: Paths.get(".")
                val watchService = FileSystems.getDefault().newWatchService()
                
                path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE
                )
                
                logger.info("开始监控配置文件变化: ${configFile.absolutePath}")
                
                while (isActive) {
                    val key = watchService.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS)
                    if (key != null) {
                        for (event in key.pollEvents()) {
                            val fileName = event.context() as Path
                            if (fileName.toString() == configFileName) {
                                logger.info("检测到配置文件变化，重新加载配置")
                                // 延迟执行以确保文件写入完成
                                delay(100)
                                loadConfig()
                            }
                        }
                        key.reset()
                    }
                    
                    // 定期检查文件修改时间（作为备选方案）
                    checkConfigFileModified()
                    delay(500)
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    logger.info("配置监控已停止")
                } else {
                    logger.severe("配置监控出错: ${e.message}")
                    e.printStackTrace()
                    // 尝试重新启动监控
                    delay(1000)
                    if (isActive) {
                        startWatchingConfig()
                    }
                }
            }
        }
    }
    
    /**
     * 检查配置文件是否被修改（轮询方式作为备选）
     */
    private fun checkConfigFileModified() {
        try {
            if (configFile.exists()) {
                val currentModifiedTime = configFile.lastModified()
                if (currentModifiedTime > lastModifiedTime.get()) {
                    logger.info("通过轮询检测到配置文件变化，重新加载配置")
                    loadConfig()
                }
            }
        } catch (e: Exception) {
            logger.warning("检查配置文件修改状态失败: ${e.message}")
        }
    }
    
    /**
     * 停止配置监控
     */
    fun stopWatchingConfig() {
        watchJob?.cancel()
        watchJob = null
    }
    
    /**
     * 关闭配置管理器
     */
    fun shutdown() {
        stopWatchingConfig()
        scope.cancel()
        logger.info("配置管理器已关闭")
    }
    
    /**
     * 获取单例实例
     */
    companion object {
        private var instance: ConfigManager? = null
        
        fun getInstance(): ConfigManager {
            if (instance == null) {
                instance = ConfigManager()
            }
            return instance!!
        }
    }
}