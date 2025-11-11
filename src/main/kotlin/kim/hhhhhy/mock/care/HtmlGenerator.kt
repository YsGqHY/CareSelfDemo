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
        /* 爱心绘制动画 */
        @keyframes drawHeart {
            0% {
                stroke-dasharray: 0 1000;
                opacity: 0;
            }
            20% {
                opacity: 1;
            }
            100% {
                stroke-dasharray: 1000 0;
                opacity: 1;
            }
        }
        /* 爱心脉动效果 */
        @keyframes pulseHeart {
            0%, 100% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.1);
            }
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
        const FADE_OUT_ANIMATION_DURATION = "${AppConfig.getInstance().fadeOutAnimationDuration.replace("s", "")}"; // 移除's'后缀，用于JavaScript动画
        const INTERVAL_MS = ${AppConfig.getInstance().popupIntervalMs};
        const TEXT_X_OFFSET = ${AppConfig.getInstance().popupTitleXOffset}; // 文本相对于弹窗容器的X轴偏移量
        const TEXT_Y_OFFSET = ${AppConfig.getInstance().popupTitleYOffset}; // 文本相对于弹窗容器的Y轴偏移量
        const CONFIGURED_MODE = "${AppConfig.getInstance().popupMode}"; // 配置文件中指定的弹窗模式
        const RANDOM_MODE_ENABLED = ${AppConfig.getInstance().randomMode}; // 是否启用随机模式选择

        /**
         * 伪随机模式选择函数
         * 使用 localStorage 存储历史记录，确保连续3次刷新不会出现重复模式
         * @returns {string} 选择的模式 (mode1, mode2, mode3)
         */
        function selectPseudoRandomMode() {
            const STORAGE_KEY = 'popup_mode_history';
            const AVAILABLE_MODES = ['mode1', 'mode2', 'mode3'];
            const HISTORY_SIZE = 2; // 记录最近2次的历史，加上本次共3次不重复

            try {
                // 从 localStorage 读取历史记录
                let history = [];
                const storedHistory = localStorage.getItem(STORAGE_KEY);
                if (storedHistory) {
                    try {
                        history = JSON.parse(storedHistory);
                        // 确保历史记录是数组且不超过指定大小
                        if (!Array.isArray(history)) {
                            history = [];
                        } else {
                            // 只保留最近的历史记录
                            history = history.slice(-HISTORY_SIZE);
                        }
                    } catch (e) {
                        console.warn('解析模式历史记录失败，重置历史记录', e);
                        history = [];
                    }
                }

                // 过滤出未使用的模式
                const availableModes = AVAILABLE_MODES.filter(mode => !history.includes(mode));

                // 如果所有模式都用过了，清空历史重新开始
                let selectedMode;
                if (availableModes.length === 0) {
                    console.log('所有模式已使用，重置历史记录');
                    history = [];
                    selectedMode = AVAILABLE_MODES[Math.floor(Math.random() * AVAILABLE_MODES.length)];
                } else {
                    // 从可用模式中随机选择一个
                    selectedMode = availableModes[Math.floor(Math.random() * availableModes.length)];
                }

                // 更新历史记录
                history.push(selectedMode);
                // 只保留最近的 HISTORY_SIZE 条记录
                if (history.length > HISTORY_SIZE) {
                    history = history.slice(-HISTORY_SIZE);
                }

                // 保存到 localStorage
                localStorage.setItem(STORAGE_KEY, JSON.stringify(history));

                console.log('伪随机模式选择 - 历史记录:', history, '选择模式:', selectedMode);

                return selectedMode;
            } catch (e) {
                console.error('伪随机模式选择失败，使用默认模式', e);
                // 如果出错，返回随机模式作为降级方案
                return AVAILABLE_MODES[Math.floor(Math.random() * AVAILABLE_MODES.length)];
            }
        }

        // 决定实际使用的弹窗模式
        const POPUP_MODE = RANDOM_MODE_ENABLED ? selectPseudoRandomMode() : CONFIGURED_MODE;
        console.log('当前使用的弹窗模式:', POPUP_MODE, '(随机模式' + (RANDOM_MODE_ENABLED ? '已启用' : '未启用') + ')');

        const MAX_POPUPS_COUNT = ${AppConfig.getInstance().maxPopupsCount}; // 模式2下最大弹窗数量
        // mode3相关配置
        const HEART_ANIMATION_DURATION = ${AppConfig.getInstance().heartAnimationDuration}; // 爱心动画持续时间
        const HEART_SIZE = ${AppConfig.getInstance().heartSize}; // 爱心大小
        const HEART_COLOR = "${AppConfig.getInstance().heartColor}"; // 爱心颜色
        const HEART_POPUP_COUNT = ${AppConfig.getInstance().heartPopupCount}; // 爱心绘制过程中显示的弹窗总数
        const HEART_SCALE = ${AppConfig.getInstance().heartScale}; // 爱心路径绘制尺寸大小的缩放因子
        const HEART_OFFSET_X = ${AppConfig.getInstance().heartOffsetX}; // 爱心图形在页面中的X坐标偏移
        const HEART_OFFSET_Y = ${AppConfig.getInstance().heartOffsetY}; // 爱心图形在页面中的Y坐标偏移
        const POPUP_TEXT_COLOR = "${AppConfig.getInstance().popupTextColor}"; // 弹窗文本颜色
        const POPUP_TEXT_STROKE_COLOR = "${AppConfig.getInstance().popupTextStrokeColor}"; // 弹窗文本描边颜色
        
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
                // 使用配置的文字颜色
                popup.style.color = POPUP_TEXT_COLOR;
                // 使用配置的文字描边颜色
                popup.style.textShadow = '2px 2px 4px ' + POPUP_TEXT_STROKE_COLOR;
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
            popup.style.transition = 'opacity ' + FADE_OUT_ANIMATION_DURATION + 's ease-out, transform 0.3s ease-out';
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
        
        // 模式3处理逻辑：中央爱心动画 - 使用算法生成爱心路径
        function handleMode3() {


            
            // 创建自适应爱心容器，铺满整个页面
            const createHeartContainer = () => {
    
                try {
                    const container = document.createElement('div');
                    container.style.position = 'fixed';
                    container.style.top = '0';
                    container.style.left = '0';
                    container.style.width = '100%';
                    container.style.height = '100%';
                    container.style.display = 'flex';
                    container.style.alignItems = 'center';
                    container.style.justifyContent = 'center';
                    container.style.zIndex = '1000';
                    container.style.overflow = 'hidden';
        
                    return container;
                } catch (error) {
                    console.error('创建爱心容器失败:', error);
                    return null;
                }
            };
            
            // 添加窗口大小变化监听，确保爱心始终自适应
            window.addEventListener('resize', () => {

                // 可以选择在窗口大小变化时重新绘制爱心
                // 这里不直接重绘，而是等待当前动画周期结束后自然适应
            });
            
            /**
             * 使用改进的参数方程算法生成标准爱心形状路径
             * 算法原理：
             * 1. 使用标准心形曲线参数方程，优化系数确保形状美观
             * 2. 增加采样点数以获得更平滑的曲线
             * 3. 优化坐标变换，确保爱心居中且比例协调
             */
            const generateHeartPath = (resolution = 200) => {
    
                try {
                    // 增加分辨率获得更平滑曲线
                    const points = [];
                    const rawPoints = []; // 保存原始坐标，用于弹窗定位
                    // 调整缩放因子以获得更标准的爱心比例
                    const scale = 1.2;
                    
                    // 对参数t从0到2π进行高密度采样
                    for (let i = 0; i <= resolution; i++) {
                        const t = (i / resolution) * 2 * Math.PI;
                        
                        // 使用标准心形曲线参数方程，优化系数
                        // 调整后的方程确保爱心形状更加标准和美观
                        const x = 16 * Math.pow(Math.sin(t), 3);
                        // 原方程可能导致底部过尖，调整系数使底部更圆润
                        const y = 13 * Math.cos(t) - 4.5 * Math.cos(2 * t) - 1.8 * Math.cos(3 * t) - 0.9 * Math.cos(4 * t);
                        
                        // 保存原始坐标，用于弹窗定位
                        rawPoints.push({ x, y, t });
                        
                        // 优化坐标变换
                        // 50是视图中心，应用缩放因子并调整位置
                        const adjustedX = 50 + x * scale;
                        // 注意这里需要翻转y轴方向（SVG坐标系y轴向下）
                        const adjustedY = 50 - y * scale;
                        
                        // 记录关键坐标点以便调试
                        if (i % 20 === 0 || i === resolution) {
            
                        }
                        
                        // 使用标准字符串拼接避免模板字符串可能的问题
                        points.push(adjustedX + ',' + adjustedY);
                    }
                    
                    // 构建SVG路径字符串，确保所有点正确连接
                    // 使用M命令移动到起点，然后L命令连接所有后续点
                    const pathString = 'M' + points.join(' L');
        
        
                    
                    // 返回路径字符串和原始点数据，用于弹窗定位
                    return { pathString, rawPoints };
                } catch (error) {
                    console.error('生成爱心路径失败:', error);
                    return { pathString: '', rawPoints: [] };
                }
            };
            
            // 定义当前爱心动画中的所有弹窗元素，用于后续清理
            let currentHeartPopups = [];
            
            // 在爱心路径点上创建并定位弹窗 - 自适应版本，确保完全铺满显示区域
            const createPopupsOnPathPoints = (rawPoints, svgElement, scale) => {
        
                
                // 清空当前弹窗列表
                currentHeartPopups = [];
                
                // 安全检查：确保svgElement和parentElement都存在
                if (!svgElement || !svgElement.parentElement) {
                    console.error('SVG元素或其父元素不存在，无法计算位置');
                    // 使用默认值作为后备方案
                    return 0;
                }
                
                // 根据弹窗尺寸和页面大小计算最佳点密度
                const popupWidth = parseInt(POPUP_WIDTH);
                const popupHeight = parseInt(POPUP_HEIGHT);
                
                // 安全获取容器尺寸
                let containerRect;
                try {
                    containerRect = svgElement.parentElement.getBoundingClientRect();
        
                } catch (error) {
                    console.error('获取容器尺寸失败，使用屏幕尺寸作为后备方案:', error);
                    // 使用屏幕尺寸作为后备方案
                    containerRect = {
                        left: 0,
                        top: 0,
                        width: window.innerWidth,
                        height: window.innerHeight
                    };
                }
                
                // 实现弹窗数量配置和平均分布算法
                // 使用用户配置的弹窗数量，如果配置为0或无效，则使用默认值
                const targetPopupCount = Math.max(10, Math.min(HEART_POPUP_COUNT, rawPoints.length));
    
                
                // 计算平均分布的步长
                const step = Math.max(1, Math.floor(rawPoints.length / targetPopupCount));
                const selectedPoints = [];
                
                // 确保均匀分布在整个路径上
                for (let i = 0; i < rawPoints.length; i += step) {
                    selectedPoints.push(rawPoints[i]);
                }
                
                // 如果点数不足，尝试补充一些点以达到目标数量
                if (selectedPoints.length < targetPopupCount && rawPoints.length > selectedPoints.length) {
                    const remainingCount = targetPopupCount - selectedPoints.length;
                    for (let i = 0; i < remainingCount; i++) {
                        const insertIndex = Math.floor((i + 0.5) * rawPoints.length / targetPopupCount);
                        if (insertIndex < rawPoints.length && selectedPoints.indexOf(rawPoints[insertIndex]) === -1) {
                            selectedPoints.push(rawPoints[insertIndex]);
                        }
                    }
                    // 按索引排序，确保绘制顺序正确
                    selectedPoints.sort((a, b) => rawPoints.indexOf(a) - rawPoints.indexOf(b));
                }
                
    
                
                // 计算爱心的中心点（页面中心）并应用偏移
                const centerX = containerRect.left + containerRect.width / 2 + HEART_OFFSET_X;
                const centerY = containerRect.top + containerRect.height / 2 + HEART_OFFSET_Y;
    
                
                // 计算自适应缩放因子，确保爱心完全铺满显示区域
                // 考虑弹窗尺寸，确保不会超出边界
                const maxAllowedSize = Math.min(
                    containerRect.width - popupWidth,
                    containerRect.height - popupHeight
                ) / 2;
                
                // 找到最远点，计算缩放比例
                let maxDistance = 0;
                for (const point of rawPoints) {
                    if (point && typeof point.x === 'number' && typeof point.y === 'number') {
                        const distance = Math.sqrt(point.x * point.x + point.y * point.y);
                        maxDistance = Math.max(maxDistance, distance);
                    }
                }
                
                // 根据最远点和最大允许尺寸计算缩放因子，确保完全铺满且不超出边界
                const adaptiveScale = maxDistance > 0 ? maxAllowedSize / maxDistance : 1;
                // 应用用户配置的爱心尺寸缩放因子
                const finalScale = adaptiveScale * HEART_SCALE;
    
                
                // 为每个选中的点创建弹窗
                selectedPoints.forEach((point, index) => {
                    // 计算弹窗在页面中的绝对位置 - 基于页面中心的相对定位
                    const popupX = centerX + (point.x * finalScale);
                    const popupY = centerY - (point.y * finalScale); // 注意Y轴翻转
                    
    
                    
                    // 延迟创建弹窗，形成动画效果
                    setTimeout(() => {
                        try {
                            // 创建与原始样式一致的弹窗
                            const popup = createPopup();
                            
                            // 精确定位弹窗，确保在正确位置
                            popup.style.position = 'fixed';
                            popup.style.left = popupX - (popupWidth / 2) + 'px';
                            popup.style.top = popupY - (popupHeight / 2) + 'px';
                            popup.style.transform = 'scale(0.8)'; // 初始缩放
                            popup.style.zIndex = '1001'; // 确保弹窗在最上层
                            
                            // 添加到当前弹窗列表，用于后续统一管理
                            currentHeartPopups.push(popup);
                            
                            // 显示弹窗动画
                            showPopupWithAnimation(popup);
                        } catch (error) {
                            console.error('创建或定位弹窗失败:', error);
                        }
                    }, index * 80); // 优化动画速度，使绘制更流畅
                });
                
        
                
                // 返回绘制完成的预计时间
                return selectedPoints.length * 80;
            };
            
            // 创建弹窗爱心形状 - 自适应版本，确保完全铺满显示区域
            const drawHeart = (container) => {
        
                try {
                    if (!container) {
                        console.error('容器为空，无法创建爱心');
                        return null;
                    }
                    
                    // 创建不可见的SVG元素，仅用于计算位置和尺寸
                    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
                    svg.style.width = '100%';
                    svg.style.height = '100%';
                    svg.setAttribute('viewBox', '0 0 100 100');
                    svg.setAttribute('preserveAspectRatio', 'xMidYMid meet');
                    svg.style.visibility = 'hidden'; // 隐藏SVG，只用于位置计算
                    
                    // 关键修复：先将SVG添加到容器中，确保parentElement不为null
                    container.appendChild(svg);
        
                    
                    // 获取容器尺寸用于自适应计算
                    const containerRect = container.getBoundingClientRect();
        
                    
                    // 增加路径分辨率以获得更平滑的爱心形状
                    const heartPathData = generateHeartPath(300); // 提高分辨率到300
                    if (!heartPathData.pathString || heartPathData.rawPoints.length === 0) {
                        console.error('生成的爱心路径数据无效');
                        return null;
                    }
                    
        
                    
                    // 创建弹窗并获取绘制完成的预计时间
                    const drawingDuration = createPopupsOnPathPoints(heartPathData.rawPoints, svg, 1.2);
                    
                    return { container, svg, drawingDuration };
                } catch (error) {
                    console.error('创建弹窗爱心形状失败:', error);
                    return null;
                }
            };
            
            // 清理所有弹窗并准备下一轮动画 - 无缝循环版本
            const cleanupHeart = (heartElements, displayTimeMs) => {
        
                try {
                    // 确保有停留时间参数
                    const actualDisplayTimeMs = displayTimeMs || 3000;
        
                    
                    if (!heartElements || !heartElements.container) {
                        console.error('要清理的爱心元素不完整');
                        // 无论如何都尝试开始下一轮动画
                        setTimeout(drawHeartAnimation, 500);
                        return;
                    }
                    
                    // 首先同时移除所有弹窗，实现统一淡出效果
        
                    currentHeartPopups.forEach((popup, index) => {
                        // 为每个弹窗添加淡出动画
                        if (popup && popup.parentNode) {
                            removePopupWithAnimation(popup);
                        }
                    });
                    
                    // 清空弹窗列表
                    setTimeout(() => {
                        currentHeartPopups = [];
            
                        
                        // 移除容器元素，释放内存
                        if (heartElements.container && heartElements.container.parentNode) {
                            heartElements.container.parentNode.removeChild(heartElements.container);
                
                        }
                        
                        // 垃圾回收提示
                        heartElements = null;
                        
                        // 立即开始下一轮动画，实现无缝循环
            
                        setTimeout(drawHeartAnimation, 100); // 最小延迟，确保DOM操作完成
                    }, FADE_OUT_TIME_MS + 100); // 等待淡出动画完成
                    
                } catch (error) {
                    console.error('清理爱心元素失败:', error);
                    // 出错时仍然尝试继续动画循环
                    setTimeout(drawHeartAnimation, 1000);
                }
            };
            
            // 爱心动画循环 - 自适应无缝循环版本
            const drawHeartAnimation = () => {
    
    
                try {
                    // 创建容器
                    const container = createHeartContainer();
                    if (!container) {
                        console.error('容器创建失败，尝试重试...');
                        setTimeout(drawHeartAnimation, 1000);
                        return;
                    }
                    
                    document.body.appendChild(container);
        
                    
                    // 创建弹窗爱心 - 现在drawHeart内部会添加SVG到容器
                    const heartElements = drawHeart(container);
                    if (!heartElements || !heartElements.svg) {
                        console.error('爱心创建失败，尝试清理并重试...');
                        if (container.parentNode) {
                            container.parentNode.removeChild(container);
                        }
                        setTimeout(drawHeartAnimation, 1000);
                        return;
                    }
                    
                    // 计算总持续时间：绘制时间 + 指定的停留时间
                    const drawingDuration = heartElements.drawingDuration || 3000; // 绘制爱心的时间
                    const displayTime = DISPLAY_TIME_MS || 2000; // 爱心停留时间，从配置中获取
                    const totalDuration = drawingDuration + displayTime;
                    
        
                    
                    // 设置清理定时器，在指定停留时间后清理并开始下一轮
                    setTimeout(() => {
                        cleanupHeart(heartElements, displayTime);
                    }, totalDuration);
                } catch (error) {
                    console.error('爱心动画循环执行失败:', error);
                    // 出错时仍然尝试继续动画循环
                    setTimeout(drawHeartAnimation, 2000);
                }
            };
            
            // 添加全局错误捕获
            window.addEventListener('error', (event) => {
                console.error('全局错误捕获:', event.error);
            });
            
            // 启动循环动画
    
            drawHeartAnimation();
    
        }
        
        // 显示弹窗
        function showPopups() {
            // 预加载图片
            preloadImages();
            
            // 根据配置的模式选择相应的处理函数
            if (POPUP_MODE === "mode2") {
                handleMode2();
            } else if (POPUP_MODE === "mode3") {
                handleMode3();
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