package org.example.flashytitles.neoforge.manager;

import org.example.flashytitles.core.model.AnimationUtil;
import org.example.flashytitles.neoforge.FlashyTitlesNeoForge;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NeoForge 显示管理器
 * 负责在 NeoForge 服务器上通过内置占位符系统显示玩家称号
 * 简化版本，使用通用接口
 */
public class DisplayManager {

    private final Object server;
    private final Map<UUID, PlayerTitleData> playerTitles = new ConcurrentHashMap<>();

    private int animationTick = 0;

    public DisplayManager(Object server) {
        this.server = server;
    }
    
    /**
     * 初始化显示管理器
     */
    public void initialize() {
        FlashyTitlesNeoForge.LOGGER.info("正在初始化 NeoForge 内置占位符显示管理器...");
        
        // 启动动画更新任务
        new Thread(() -> {
            while (true) { // 简化的循环条件
                try {
                    Thread.sleep(500); // 0.5秒更新一次
                    animationTick++;
                    if (animationTick >= Integer.MAX_VALUE - 1000) {
                        animationTick = 0;
                    }
                    updateAnimatedTitles();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
        
        FlashyTitlesNeoForge.LOGGER.info("NeoForge 内置占位符显示管理器初始化完成");
    }
    
    /**
     * 关闭显示管理器
     */
    public void shutdown() {
        FlashyTitlesNeoForge.LOGGER.info("正在关闭 NeoForge 显示管理器...");
        
        // 清理所有玩家的称号显示
        // 简化版本，只清理内存数据
        FlashyTitlesNeoForge.LOGGER.info("清理 {} 个玩家的称号数据", playerTitles.size());
        
        playerTitles.clear();
        
        FlashyTitlesNeoForge.LOGGER.info("NeoForge 显示管理器已关闭");
    }
    
    /**
     * 设置玩家称号
     */
    public void setPlayerTitle(Object player, String titleId, String titleText, boolean animated) {
        // 简化版本，使用 UUID 作为键
        String playerName = player.toString(); // 简化的玩家名获取
        UUID uuid = UUID.nameUUIDFromBytes(playerName.getBytes()); // 生成UUID

        // 移除旧的称号显示
        removePlayerTitle(player);

        if (titleText == null || titleText.trim().isEmpty()) {
            return;
        }

        // 保存称号数据（内置占位符系统会从这里读取）
        PlayerTitleData titleData = new PlayerTitleData(titleId, titleText, animated);
        playerTitles.put(uuid, titleData);

        FlashyTitlesNeoForge.LOGGER.info("为玩家 {} 设置称号: {} (动画: {}) [内置占位符]", playerName, titleId, animated);
    }
    
    /**
     * 移除玩家称号
     */
    public void removePlayerTitle(Object player) {
        String playerName = player.toString();
        UUID uuid = UUID.nameUUIDFromBytes(playerName.getBytes());

        // 移除称号数据（内置占位符系统会自动返回空值）
        playerTitles.remove(uuid);

        FlashyTitlesNeoForge.LOGGER.debug("移除玩家 {} 的称号显示 [内置占位符]", playerName);
    }
    

    
    /**
     * 更新动画称号
     * 内置占位符模式下，动画文本会在占位符请求时实时渲染
     */
    private void updateAnimatedTitles() {
        // 在内置占位符模式下，我们只需要更新tick计数器
        // 实际的动画渲染在占位符方法中进行
        for (Map.Entry<UUID, PlayerTitleData> entry : playerTitles.entrySet()) {
            PlayerTitleData titleData = entry.getValue();

            if (!titleData.isAnimated()) {
                continue;
            }

            // 在内置占位符模式下，动画文本会在占位符请求时实时计算
            // 这里只需要确保数据是最新的
        }
    }
    
    /**
     * 获取玩家当前称号数据
     */
    public PlayerTitleData getPlayerTitleData(UUID uuid) {
        return playerTitles.get(uuid);
    }
    
    /**
     * 检查玩家是否有称号
     */
    public boolean hasTitle(UUID uuid) {
        return playerTitles.containsKey(uuid);
    }

    /**
     * 获取玩家称号文本（用于内置占位符系统）
     */
    public String getPlayerTitleText(UUID uuid) {
        PlayerTitleData titleData = playerTitles.get(uuid);
        if (titleData == null) {
            return null;
        }

        if (titleData.isAnimated()) {
            // 返回动画渲染后的文本
            return AnimationUtil.renderAnimatedText(titleData.getRawText(), animationTick);
        } else {
            // 返回静态文本
            return titleData.getRawText();
        }
    }

    /**
     * 获取玩家称号ID（用于内置占位符系统）
     */
    public String getPlayerTitleId(UUID uuid) {
        PlayerTitleData titleData = playerTitles.get(uuid);
        return titleData != null ? titleData.getTitleId() : null;
    }

    /**
     * 获取当前动画tick（用于外部调用）
     */
    public int getCurrentAnimationTick() {
        return animationTick;
    }

    /**
     * 处理占位符请求（内置占位符系统）
     */
    public String processPlaceholder(UUID playerUuid, String placeholder) {
        if (playerUuid == null || placeholder == null) {
            return "";
        }

        return switch (placeholder.toLowerCase()) {
            case "flashytitles_title" -> {
                String titleText = getPlayerTitleText(playerUuid);
                yield titleText != null ? titleText : "";
            }
            case "flashytitles_title_raw" -> {
                String titleText = getPlayerTitleText(playerUuid);
                yield titleText != null ? titleText : "";
            }
            case "flashytitles_title_id" -> {
                String titleId = getPlayerTitleId(playerUuid);
                yield titleId != null ? titleId : "";
            }
            case "flashytitles_has_title" -> {
                boolean hasTitle = hasTitle(playerUuid);
                yield hasTitle ? "true" : "false";
            }
            case "flashytitles_title_with_space" -> {
                String titleText = getPlayerTitleText(playerUuid);
                yield titleText != null && !titleText.isEmpty() ? titleText + " " : "";
            }
            case "flashytitles_title_prefix" -> {
                String titleText = getPlayerTitleText(playerUuid);
                yield titleText != null ? titleText : "";
            }
            default -> "";
        };
    }
    
    /**
     * 玩家称号数据类
     */
    public static class PlayerTitleData {
        private final String titleId;
        private final String rawText;
        private final boolean animated;
        
        public PlayerTitleData(String titleId, String rawText, boolean animated) {
            this.titleId = titleId;
            this.rawText = rawText;
            this.animated = animated;
        }
        
        public String getTitleId() { return titleId; }
        public String getRawText() { return rawText; }
        public boolean isAnimated() { return animated; }
    }
}
