package org.example.flashytitles.fabric.manager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.example.flashytitles.core.model.AnimationUtil;
import org.example.flashytitles.fabric.FlashyTitlesFabric;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabric 显示管理器
 * 负责在 Fabric 服务器上显示玩家称号
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
        FlashyTitlesFabric.LOGGER.info("正在初始化 Fabric 显示管理器...");
        
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
        
        FlashyTitlesFabric.LOGGER.info("Fabric 显示管理器初始化完成");
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
        
        // 保存称号数据
        PlayerTitleData titleData = new PlayerTitleData(titleId, titleText, animated);
        playerTitles.put(uuid, titleData);
        
        // 应用称号显示
        applyTitleDisplay(player, titleText);
        
        FlashyTitlesFabric.LOGGER.info("为玩家 {} 设置称号: {} (动画: {})", player.getName().getString(), titleId, animated);
    }
    
    /**
     * 移除玩家称号
     */
    public void removePlayerTitle(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        
        // 移除称号数据
        playerTitles.remove(uuid);
        
        // 移除团队显示
        ServerScoreboard scoreboard = server.getScoreboard();
        String teamName = "ft_" + uuid.toString().substring(0, 8);
        Team team = scoreboard.getTeam(teamName);
        
        if (team != null) {
            if (team.getPlayerList().contains(player.getName().getString())) {
                scoreboard.removeScoreHolderFromTeam(player.getName().getString(), team);
            }
            if (team.getPlayerList().isEmpty()) {
                scoreboard.removeTeam(team);
            }
        }
        
        FlashyTitlesFabric.LOGGER.debug("移除玩家 {} 的称号显示", player.getName().getString());
    }
    
    /**
     * 应用称号显示
     */
    private void applyTitleDisplay(ServerPlayerEntity player, String titleText) {
        ServerScoreboard scoreboard = server.getScoreboard();
        
        // 创建或获取团队
        String teamName = "ft_" + player.getUuid().toString().substring(0, 8);
        Team team = scoreboard.getTeam(teamName);
        
        if (team == null) {
            team = scoreboard.addTeam(teamName);
        }
        
        // 设置前缀（称号）
        String prefix = titleText;
        if (prefix.length() > 64) {
            prefix = prefix.substring(0, 64); // 限制长度
        }
        
        team.setPrefix(Text.literal(prefix + " "));
        
        // 添加玩家到团队
        if (!team.getPlayerList().contains(player.getName().getString())) {
            scoreboard.addScoreHolderToTeam(player.getName().getString(), team);
        }
    }
    
    /**
     * 更新动画称号
     */
    private void updateAnimatedTitles() {
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
            
            // 渲染动画文本
            String animatedText = AnimationUtil.renderAnimatedText(titleData.getRawText(), animationTick);
            
            // 更新显示
            ServerScoreboard scoreboard = server.getScoreboard();
            String teamName = "ft_" + uuid.toString().substring(0, 8);
            Team team = scoreboard.getTeam(teamName);
            
            if (team != null) {
                String prefix = animatedText;
                if (prefix.length() > 64) {
                    prefix = prefix.substring(0, 64);
                }
                team.setPrefix(Text.literal(prefix + " "));
            }
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
