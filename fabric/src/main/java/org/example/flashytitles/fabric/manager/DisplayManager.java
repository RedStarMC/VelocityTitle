package org.example.flashytitles.fabric.manager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.example.flashytitles.core.model.AnimationUtil;
import org.example.flashytitles.fabric.FlashyTitlesFabric;
import org.example.flashytitles.fabric.model.PlayerTitleData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabric 显示管理器
 * 负责在 Fabric 服务器上通过 Text Placeholder API 显示玩家称号
 */
public class DisplayManager {

    private final MinecraftServer server;
    private final Map<UUID, PlayerTitleData> playerTitles = new ConcurrentHashMap<>();

    private int animationTick = 0;
    
    public DisplayManager(MinecraftServer server) {
        this.server = server;
    }
    
    /**
     * 初始化显示管理器
     */
    public void initialize() {
        FlashyTitlesFabric.LOGGER.info("正在初始化 Fabric Text Placeholder API 显示管理器...");

        // 启动动画更新任务 - 每10tick（0.5秒）更新一次
        // 使用简单的线程来处理动画更新
        new Thread(() -> {
            while (!server.isStopped()) {
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

        FlashyTitlesFabric.LOGGER.info("Fabric Text Placeholder API 显示管理器初始化完成");
    }
    
    /**
     * 关闭显示管理器
     */
    public void shutdown() {
        FlashyTitlesFabric.LOGGER.info("正在关闭 Fabric 显示管理器...");
        
        // 清理所有玩家的称号显示
        for (UUID uuid : playerTitles.keySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                removePlayerTitle(player);
            }
        }
        
        playerTitles.clear();
        
        FlashyTitlesFabric.LOGGER.info("Fabric 显示管理器已关闭");
    }
    
    /**
     * 设置玩家称号
     */
    public void setPlayerTitle(ServerPlayerEntity player, String titleId, String titleText, boolean animated) {
        UUID uuid = player.getUuid();

        // 移除旧的称号显示
        removePlayerTitle(player);

        if (titleText == null || titleText.trim().isEmpty()) {
            return;
        }

        // 保存称号数据（Text Placeholder API会从这里读取）
        PlayerTitleData titleData = new PlayerTitleData(titleId, titleText, animated);
        playerTitles.put(uuid, titleData);

        FlashyTitlesFabric.LOGGER.info("为玩家 {} 设置称号: {} (动画: {}) [Text Placeholder API]", player.getName().getString(), titleId, animated);
    }
    
    /**
     * 移除玩家称号
     */
    public void removePlayerTitle(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        // 移除称号数据（Text Placeholder API会自动返回空值）
        playerTitles.remove(uuid);

        FlashyTitlesFabric.LOGGER.debug("移除玩家 {} 的称号显示 [Text Placeholder API]", player.getName().getString());
    }
    

    
    /**
     * 更新动画称号
     * Text Placeholder API模式下，动画文本会在占位符请求时实时渲染
     */
    private void updateAnimatedTitles() {
        // 在Text Placeholder API模式下，我们只需要更新tick计数器
        // 实际的动画渲染在占位符扩展中进行
        for (Map.Entry<UUID, PlayerTitleData> entry : playerTitles.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerTitleData titleData = entry.getValue();

            if (!titleData.isAnimated()) {
                continue;
            }

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null) {
                continue;
            }

            // 在Text Placeholder API模式下，动画文本会在占位符请求时实时计算
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
     * 获取玩家称号文本（用于Text Placeholder API）
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
     * 获取玩家称号ID（用于Text Placeholder API）
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

}
