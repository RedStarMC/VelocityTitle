package org.example.flashytitles.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.example.flashytitles.spigot.manager.DisplayManager;
import org.example.flashytitles.spigot.sync.SyncHandler;

/**
 * Spigot 玩家事件监听器
 */
public class PlayerListener implements Listener {
    
    private final DisplayManager displayManager;
    private final SyncHandler syncHandler;
    
    public PlayerListener(DisplayManager displayManager, SyncHandler syncHandler) {
        this.displayManager = displayManager;
        this.syncHandler = syncHandler;
    }
    
    /**
     * 玩家加入事件
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 延迟请求玩家数据，确保玩家完全加载
        player.getServer().getScheduler().runTaskLater(
            player.getServer().getPluginManager().getPlugin("FlashyTitles"),
            () -> syncHandler.requestPlayerData(player),
            20L // 1秒后
        );
    }
    
    /**
     * 玩家退出事件
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 清理玩家的称号显示
        displayManager.removePlayerTitle(player);
    }
}
