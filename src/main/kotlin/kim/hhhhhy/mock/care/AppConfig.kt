package kim.hhhhhy.mock.care

/**
 * 应用配置对象，集中管理所有可自定义的配置选项
 */
object AppConfig {
    // 服务器配置
    const val DEFAULT_PORT = 7789
    const val HOST = "0.0.0.0"
    
    // 弹窗配置
    const val POPUP_WIDTH = "300px"
    const val POPUP_HEIGHT = "140px"
    const val POPUP_DISPLAY_TIME_MS = 5000 // 弹窗显示时间（毫秒）
    const val POPUP_FADE_OUT_TIME_MS = 500 // 弹窗淡出动画时间（毫秒）
    const val POPUP_INTERVAL_MS = 150 // 弹窗生成间隔（毫秒）
    const val FADE_IN_ANIMATION_DURATION = "0.3s" // 弹窗淡入动画持续时间
    const val FADE_OUT_ANIMATION_DURATION = "0.5s" // 弹窗淡出动画持续时间
    
    // 背景贴图配置
    // 页面背景贴图URL列表，默认为空列表，可在运行时更新
    @Volatile
    var PAGE_BACKGROUND_IMAGES: List<String> = listOf()
    
    // 弹窗背景贴图URL列表，默认为空列表，可在运行时更新
    @Volatile
    var POPUP_BACKGROUND_IMAGES: List<String> = listOf()
    
    /**
     * 更新页面背景图片列表
     */
    fun updatePageBackgroundImages(images: List<String>) {
        PAGE_BACKGROUND_IMAGES = images.toList()
    }
    
    /**
     * 更新弹窗背景图片列表
     */
    fun updatePopupBackgroundImages(images: List<String>) {
        POPUP_BACKGROUND_IMAGES = images.toList()
    }
    
    // 应用标题
    const val APP_TITLE = "好好爱自己"
    
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
        "好好吃饭"
    )
}