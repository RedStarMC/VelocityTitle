package org.example.flashytitles.velocity.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.example.flashytitles.velocity.manager.TitleManager;
import org.example.flashytitles.velocity.sync.SyncManager;
import org.slf4j.Logger;

/**
 * 玩家事件监听器
 */
public class PlayerListener {
    
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("flashytitles", "sync");
    
    private final TitleManager titleManager;
    private final SyncManager syncManager;
    private final Logger logger;
    
    public PlayerListener(TitleManager titleManager, SyncManager syncManager, Logger logger) {
        this.titleManager = titleManager;
        this.syncManager = syncManager;
        this.logger = logger;
    }
    
    /**
     * 玩家连接到服务器时
     */
    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        RegisteredServer server = event.getServer();
        
        logger.debug("玩家 {} 连接到服务器 {}", player.getUsername(), server.getServerInfo().getName());
        
        // 确保玩家有初始金币
        if (titleManager.getCoins(player.getUniqueId()) == 0) {
            // 这里可以设置初始金币，但需要从配置中获取
            // titleManager.setCoins(player.getUniqueId(), configManager.getDefaultStartingCoins());
        }
        
        // 同步玩家称号到新服务器
        syncManager.onPlayerJoin(player);
    }
    
    /**
     * 玩家断开连接时
     */
    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        
        logger.debug("玩家 {} 断开连接", player.getUsername());
        
        // 通知其他服务器玩家离开
        syncManager.onPlayerQuit(player);
    }
    
    /**
     * 处理插件消息
     */
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) {
            return;
        }
        
        // 确保消息来源是服务器
        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }
        
        // 确保目标是玩家
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getTarget();
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        
        try {
            syncManager.handlePluginMessage(player, in);
        } catch (Exception e) {
            logger.error("处理插件消息时发生错误", e);
        }
        
        // 阻止消息继续传播
        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}
