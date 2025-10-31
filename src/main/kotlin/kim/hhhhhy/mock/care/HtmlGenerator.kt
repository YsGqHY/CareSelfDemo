package kim.hhhhhy.mock.care


/**
 * HTML生成器，负责生成前端页面
 */
object HtmlGenerator {
    /**
     * 创建包含弹窗功能的HTML页面
     */
    fun createHtmlPage(): String {
        return """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${AppConfig.APP_TITLE}</title>
    <style>
        body {
            font-family: 'Microsoft YaHei', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f0f0f0;
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
            animation: fadeIn ${AppConfig.FADE_IN_ANIMATION_DURATION} ease-in;
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
        const tips = ${AppConfig.TIPS.toJson()};
        
        // 配置常量
        const POPUP_WIDTH = "${AppConfig.POPUP_WIDTH}";
        const POPUP_HEIGHT = "${AppConfig.POPUP_HEIGHT}";
        const DISPLAY_TIME_MS = ${AppConfig.POPUP_DISPLAY_TIME_MS};
        const FADE_OUT_TIME_MS = ${AppConfig.POPUP_FADE_OUT_TIME_MS};
        const INTERVAL_MS = ${AppConfig.POPUP_INTERVAL_MS};
        
        // 生成随机位置
        function getRandomPosition() {
            const width = window.innerWidth;
            const height = window.innerHeight;
            const x = Math.floor(Math.random() * (width - parseInt(POPUP_WIDTH)));
            const y = Math.floor(Math.random() * (height - parseInt(POPUP_HEIGHT)));
            return { x, y };
        }
        
        // 生成随机颜色
        function getRandomColor() {
            const r = Math.floor(Math.random() * 255);
            const g = Math.floor(Math.random() * 255);
            const b = Math.floor(Math.random() * 255);
            return 'rgb(' + r + ',' + g + ',' + b + ')';
        }
        
        // 显示弹窗
        function showPopups() {
            const showNextPopup = () => {
                // 随机选择提示文本
                const tip = tips[Math.floor(Math.random() * tips.length)];
                
                // 创建弹窗元素
                const popup = document.createElement('div');
                popup.className = 'popup';
                popup.textContent = tip;
                popup.style.backgroundColor = getRandomColor();
                popup.style.width = POPUP_WIDTH;
                popup.style.height = POPUP_HEIGHT;
                popup.style.transition = `opacity ${AppConfig.FADE_OUT_ANIMATION_DURATION} ease-out`;
                
                // 设置随机位置
                const position = getRandomPosition();
                popup.style.left = position.x + 'px';
                popup.style.top = position.y + 'px';
                
                // 添加到页面
                document.body.appendChild(popup);
                
                // 设置自动移除逻辑
                setTimeout(() => {
                    // 开始淡出动画
                    popup.style.opacity = '0';
                    
                    // 动画结束后移除元素
                    setTimeout(() => {
                        if (popup.parentNode) {
                            popup.parentNode.removeChild(popup);
                        }
                    }, FADE_OUT_TIME_MS);
                }, DISPLAY_TIME_MS);
                
                // 设置下一个弹窗的显示时间
                setTimeout(showNextPopup, INTERVAL_MS);
            };
            
            // 开始显示第一个弹窗
            showNextPopup();
        }
        
        // 页面加载后自动执行动画
        window.onload = showPopups;
    </script>
</body>
</html>
        """.trimIndent()
    }
}