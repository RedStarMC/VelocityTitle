package org.example.flashytitles.spigot.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.example.flashytitles.core.model.AnimationUtil;
import org.example.flashytitles.spigot.model.PlayerTitleData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 显示管理器
 * 负责在 Spigot 服务器上通过 PAPI 显示玩家称号
 */
public class DisplayManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerTitleData> playerTitles = new ConcurrentHashMap<>();

    private BukkitTask animationTask;
    private int animationTick = 0;
    
    public DisplayManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 初始化显示管理器
     */
    public void initialize() {
        plugin.getLogger().info("正在初始化PAPI显示管理器...");

        // 启动动画更新任务（每10tick更新一次，即0.5秒）
        animationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            animationTick++;
            if (animationTick >= Integer.MAX_VALUE - 1000) {
                animationTick = 0; // 防止溢出
            }
            updateAnimatedTitles();
        }, 0L, 10L);

        plugin.getLogger().info("PAPI显示管理器初始化完成");
    }
    
    /**
     * 关闭显示管理器
     */
    public void shutdown() {
        plugin.getLogger().info("正在关闭显示管理器...");
        
        if (animationTask != null && !animationTask.isCancelled()) {
            animationTask.cancel();
        }
        
        // 清理所有玩家的称号显示
        for (UUID uuid : playerTitles.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                removePlayerTitle(player);
            }
        }
        
        playerTitles.clear();
        
        plugin.getLogger().info("显示管理器已关闭");
    }
    
    /**
     * 设置玩家称号
     */
    public void setPlayerTitle(Player player, String titleId, String titleText, boolean animated) {
        UUID uuid = player.getUniqueId();

        // 移除旧的称号显示
        removePlayerTitle(player);

        if (titleText == null || titleText.trim().isEmpty()) {
            return;
        }

        // 保存称号数据（PAPI会从这里读取）
        PlayerTitleData titleData = new PlayerTitleData(titleId, titleText, animated);
        playerTitles.put(uuid, titleData);

        plugin.getLogger().info("为玩家 " + player.getName() + " 设置称号: " + titleId + " (动画: " + animated + ") [PAPI]");
    }
    
    /**
     * 移除玩家称号
     */
    public void removePlayerTitle(Player player) {
        UUID uuid = player.getUniqueId();

        // 移除称号数据（PAPI会自动返回空值）
        playerTitles.remove(uuid);

        plugin.getLogger().info("移除玩家 " + player.getName() + " 的称号显示 [PAPI]");
    }
    

    
    /**
     * 更新动画称号
     * PAPI模式下，动画文本会在PAPI请求时实时渲染
     */
    private void updateAnimatedTitles() {
        // 在PAPI模式下，我们只需要更新tick计数器
        // 实际的动画渲染在PAPI扩展中进行
        for (Map.Entry<UUID, PlayerTitleData> entry : playerTitles.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerTitleData titleData = entry.getValue();

            if (!titleData.isAnimated()) {
                continue;
            }

            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                continue;
            }

            // 在PAPI模式下，动画文本会在占位符请求时实时计算
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
     * 获取玩家称号文本（用于PAPI）
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
     * 获取玩家称号ID（用于PAPI）
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
