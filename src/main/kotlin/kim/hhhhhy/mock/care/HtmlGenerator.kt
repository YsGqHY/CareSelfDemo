package kim.hhhhhy.mock.care


/**
 * HTML生成器，负责生成前端页面
 */
object HtmlGenerator {
    /**
     * 创建包含弹窗功能的HTML页面
     */
    fun createHtmlPage(): String {
        // 从配置列表中随机选择一个页面背景图URL
        val randomPageBgImage = if (AppConfig.getInstance().pageBackgroundImages.isNotEmpty()) {
            AppConfig.getInstance().pageBackgroundImages.random()
        } else ""
        
        return """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${AppConfig.getInstance().appTitle}</title>
    <style>
        body {
            font-family: 'Microsoft YaHei', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f0f0f0;
            ${if (randomPageBgImage.isNotEmpty()) "background-image: url('$randomPageBgImage'); background-size: cover; background-position: center;" else ""}
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
            animation: fadeIn ${AppConfig.getInstance().fadeInAnimationDuration} ease-in, scaleIn ${AppConfig.getInstance().fadeInAnimationDuration} ease-out;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3); /* 添加阴影效果 */
        }
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        @keyframes scaleIn {
            from { transform: scale(0.8); }
            to { transform: scale(1); }
        }
    </style>
</head>
<body>
    <script>
        // 提示信息列表
        const tips = ${AppConfig.getInstance().tips.toJson()};
        
        // 配置常量
        const POPUP_WIDTH = "${AppConfig.getInstance().popupWidth}";
        const POPUP_HEIGHT = "${AppConfig.getInstance().popupHeight}";
        const DISPLAY_TIME_MS = ${AppConfig.getInstance().popupDisplayTimeMs};
        const FADE_OUT_TIME_MS = ${AppConfig.getInstance().popupFadeOutTimeMs};
        const INTERVAL_MS = ${AppConfig.getInstance().popupIntervalMs};
        const TEXT_X_OFFSET = ${AppConfig.getInstance().popupTitleXOffset}; // 文本相对于弹窗容器的X轴偏移量
        const TEXT_Y_OFFSET = ${AppConfig.getInstance().popupTitleYOffset}; // 文本相对于弹窗容器的Y轴偏移量
        const POPUP_MODE = "${AppConfig.getInstance().popupMode}"; // 弹窗模式
        const MAX_POPUPS_COUNT = ${AppConfig.getInstance().maxPopupsCount}; // 模式2下最大弹窗数量
        
        // 弹窗队列，用于模式2的FIFO管理
        const popupQueue = []; // 按创建顺序存储弹窗元素
        // 页面背景贴图URL
        const PAGE_BACKGROUND_IMAGE = "$randomPageBgImage";
        // 弹窗背景贴图URL列表
        const POPUP_BACKGROUND_IMAGES = ${AppConfig.getInstance().popupBackgroundImages.toJson()};
        
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
        
        // 从列表中随机选择一个图片URL
        function getRandomImageFromList(imageList) {
            if (!Array.isArray(imageList) || imageList.length === 0) {
                return '';
            }
            return imageList[Math.floor(Math.random() * imageList.length)];
        }
        
        // 设置弹窗背景
        function setPopupBackground(popup) {
            const randomPopupBgImage = getRandomImageFromList(POPUP_BACKGROUND_IMAGES);
            if (randomPopupBgImage) {
                // 优先使用随机选择的背景贴图
                popup.style.backgroundImage = 'url(' + randomPopupBgImage + ')';
                // 使用contain代替cover以确保完整显示图片内容，避免重要部分被裁剪
                popup.style.backgroundSize = 'contain';
                popup.style.backgroundPosition = 'center';
                popup.style.backgroundRepeat = 'no-repeat';
                // 添加背景色作为过渡区域
                popup.style.backgroundColor = '#f0f0f0';
                // 确保文字颜色为白色以适应各种背景
                popup.style.color = 'white';
                // 添加更强的文字阴影以提高可读性
                popup.style.textShadow = '2px 2px 4px rgba(0, 0, 0, 0.9)';
                // 添加边框以提高弹窗的可见性
                popup.style.border = '2px solid rgba(255, 255, 255, 0.3)';
            } else {
                // 使用随机颜色作为降级方案
                popup.style.backgroundColor = getRandomColor();
                popup.style.backgroundImage = 'none';
                // 重置边框样式
                popup.style.border = 'none';
            }
        }
        
        // 预加载背景图片
        function preloadImages() {
            // 预加载页面背景图
            if (PAGE_BACKGROUND_IMAGE) {
                const img = new Image();
                img.onload = function() {
                    console.log('页面背景图加载成功');
                };
                img.onerror = function() {
                    console.warn('页面背景图加载失败，使用默认背景');
                    document.body.style.backgroundImage = 'none';
                    document.body.style.backgroundColor = '#f0f0f0';
                };
                img.src = PAGE_BACKGROUND_IMAGE;
            }
            
            // 预加载所有弹窗背景图
            if (Array.isArray(POPUP_BACKGROUND_IMAGES) && POPUP_BACKGROUND_IMAGES.length > 0) {
                POPUP_BACKGROUND_IMAGES.forEach((imageUrl, index) => {
                    const img = new Image();
                    img.onload = function() {
                        console.log('弹窗背景图 ' + (index + 1) + ' 加载成功: ' + imageUrl);
                    };
                    img.onerror = function() {
                        console.warn('弹窗背景图 ' + (index + 1) + ' 加载失败: ' + imageUrl);
                        // 从数组中移除加载失败的图片
                        const imageIndex = POPUP_BACKGROUND_IMAGES.indexOf(imageUrl);
                        if (imageIndex > -1) {
                            POPUP_BACKGROUND_IMAGES.splice(imageIndex, 1);
                        }
                    };
                    img.src = imageUrl;
                });
            }
        }
        
        // 创建弹窗
        function createPopup() {
            // 随机选择提示文本
            const tip = tips[Math.floor(Math.random() * tips.length)];
            
            // 创建弹窗元素
            const popup = document.createElement('div');
            popup.className = 'popup';
            popup.style.width = POPUP_WIDTH;
            popup.style.height = POPUP_HEIGHT;
            popup.style.transition = `opacity ${AppConfig.getInstance().fadeOutAnimationDuration} ease-out, transform 0.3s ease-out`;
            popup.style.opacity = '0'; // 初始透明度为0，准备淡入动画
            popup.style.transform = 'scale(0.8)'; // 初始缩放效果
            
            // 设置弹窗背景
            setPopupBackground(popup);
            
            // 设置随机位置（弹窗容器在页面中的位置）
            const position = getRandomPosition();
            popup.style.left = position.x + 'px';
            popup.style.top = position.y + 'px';
            
            // 创建文本容器，用于应用文本偏移
            const textContainer = document.createElement('div');
            textContainer.textContent = tip;
            textContainer.style.transform = 'translate(' + TEXT_X_OFFSET + 'px, ' + TEXT_Y_OFFSET + 'px)';
            
            // 清空弹窗内容并添加带有偏移的文本容器
            popup.innerHTML = '';
            popup.appendChild(textContainer);
            
            // 调整弹窗样式，使其成为flex容器，为内部元素提供正确的定位上下文
            popup.style.display = 'flex';
            popup.style.alignItems = 'center';
            popup.style.justifyContent = 'center';
            
            return popup;
        }
        
        // 显示弹窗动画
        function showPopupWithAnimation(popup) {
            document.body.appendChild(popup);
            
            // 触发重排，确保动画能正常播放
            void popup.offsetWidth;
            
            // 执行淡入和放大动画
            popup.style.opacity = '1';
            popup.style.transform = 'scale(1)';
        }
        
        // 移除弹窗动画
        function removePopupWithAnimation(popup) {
            popup.style.opacity = '0';
            popup.style.transform = 'scale(0.8)';
            
            // 动画结束后移除元素
            setTimeout(() => {
                if (popup.parentNode) {
                    popup.parentNode.removeChild(popup);
                }
            }, FADE_OUT_TIME_MS);
        }
        
        // 模式1处理逻辑：定时消失
        function handleMode1() {
            const showNextPopup = () => {
                const popup = createPopup();
                showPopupWithAnimation(popup);
                
                // 设置自动移除逻辑
                setTimeout(() => {
                    removePopupWithAnimation(popup);
                }, DISPLAY_TIME_MS);
                
                // 设置下一个弹窗的显示时间
                setTimeout(showNextPopup, INTERVAL_MS);
            };
            
            showNextPopup();
        }
        
        // 模式2处理逻辑：常驻，数量限制，FIFO清理
        function handleMode2() {
            const showNextPopup = () => {
                // 检查是否需要清理旧弹窗（FIFO原则）
                if (popupQueue.length >= MAX_POPUPS_COUNT) {
                    const oldestPopup = popupQueue.shift(); // 移除最早的弹窗
                    removePopupWithAnimation(oldestPopup);
                }
                
                const popup = createPopup();
                showPopupWithAnimation(popup);
                
                // 将新弹窗添加到队列末尾
                popupQueue.push(popup);
                
                // 设置下一个弹窗的显示时间
                setTimeout(showNextPopup, INTERVAL_MS);
            };
            
            showNextPopup();
        }
        
        // 显示弹窗
        function showPopups() {
            // 预加载图片
            preloadImages();
            
            // 根据配置的模式选择相应的处理函数
            if (POPUP_MODE === "mode2") {
                handleMode2();
            } else {
                // 默认为模式1
                handleMode1();
            }
        }
        
        // 页面加载后自动执行动画
        window.onload = showPopups;
    </script>
</body>
</html>
        """.trimIndent()
    }
}