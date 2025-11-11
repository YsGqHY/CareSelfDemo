package kim.hhhhhy.mock.care

/**
 * 应用配置对象，集中管理所有可自定义的配置选项
 */
class AppConfig {
    // 服务器配置
    var defaultPort: Int = 7789
    var host: String = "0.0.0.0"
    
    // 弹窗配置
    var popupWidth: String = "300px"
    var popupHeight: String = "140px"
    var popupDisplayTimeMs: Int = 5000 // 弹窗显示时间（毫秒）
    var popupFadeOutTimeMs: Int = 500 // 弹窗淡出动画时间（毫秒）
    var popupIntervalMs: Int = 150 // 弹窗生成间隔（毫秒）
    var fadeInAnimationDuration: String = "0.3s" // 弹窗淡入动画持续时间
    var fadeOutAnimationDuration: String = "0.5s" // 弹窗淡出动画持续时间
    var popupTitleXOffset: Int = 0 // 弹窗标题X轴偏移量（像素），相对于弹窗容器内部，正值向右偏移
    var popupTitleYOffset: Int = 0 // 弹窗标题Y轴偏移量（像素），相对于弹窗容器内部，正值向下偏移
    var popupTextColor: String = "white" // 弹窗文本颜色
    var popupTextStrokeColor: String = "rgba(0, 0, 0, 0.9)" // 弹窗文本描边颜色
    var popupMode: String = "mode1" // 弹窗模式，可选值：mode1（默认，定时消失）、mode2（常驻，数量限制）、mode3（中央爱心动画）
    var randomMode: Boolean = false // 是否启用随机模式选择，启用后每次刷新页面会随机选择一种显示模式
    var heartAnimationDuration: Int = 2000 // 模式3下爱心动画持续时间（毫秒）
    var heartSize: Int = 150 // 模式3下爱心大小（像素）
    var heartColor: String = "#ff3e6c" // 模式3下爱心颜色（默认粉红色）
    var heartPopupCount: Int = 30 // 模式3下爱心绘制过程中显示的弹窗总数
    var heartScale: Double = 1.0 // 模式3下爱心路径绘制尺寸大小的缩放因子
    var heartOffsetX: Int = 0 // 模式3下爱心图形在页面中的X坐标偏移
    var heartOffsetY: Int = 0 // 模式3下爱心图形在页面中的Y坐标偏移
    var maxPopupsCount: Int = 10 // 模式2下最大同时显示的弹窗数量
    
    // 背景贴图配置
    // 页面背景贴图URL列表，默认为空列表，可在运行时更新
    @Volatile
    var pageBackgroundImages: MutableList<String> = mutableListOf()
    
    // 弹窗背景贴图URL列表，默认为空列表，可在运行时更新
    @Volatile
    var popupBackgroundImages: MutableList<String> = mutableListOf()
    
    // 应用标题
    var appTitle: String = "好好爱自己"
    
    // 提示信息列表
    var tips: MutableList<String> = mutableListOf(
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
    
    /**
     * 更新页面背景图片列表
     */
    fun updatePageBackgroundImages(images: List<String>) {
        pageBackgroundImages = images.toMutableList()
    }
    
    /**
     * 更新弹窗背景图片列表
     */
    fun updatePopupBackgroundImages(images: List<String>) {
        popupBackgroundImages = images.toMutableList()
    }
    
    /**
     * 获取单例实例
     */
    companion object {
        private var instance: AppConfig? = null
        
        fun getInstance(): AppConfig {
            if (instance == null) {
                instance = AppConfig()
            }
            return instance!!
        }
        
        fun setInstance(newInstance: AppConfig) {
            instance = newInstance
        }
    }
}