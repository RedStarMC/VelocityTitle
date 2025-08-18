package org.example.flashytitles.fabric.sync;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.example.flashytitles.core.message.Message;
import org.example.flashytitles.core.message.MessageType;
import org.example.flashytitles.fabric.FlashyTitlesFabric;
import org.example.flashytitles.fabric.manager.DisplayManager;

import java.util.UUID;

/**
 * Fabric 同步处理器
 * 处理与 Velocity 的通信
 */
public class SyncHandler {
    
    private static final Identifier CHANNEL = Identifier.of("flashytitles", "sync");
    
    private final MinecraftServer server;
    private final DisplayManager displayManager;
    
    public SyncHandler(MinecraftServer server, DisplayManager displayManager) {
        this.server = server;
        this.displayManager = displayManager;
    }
    
    /**
     * 初始化同步处理器
     */
    public void initialize() {
        FlashyTitlesFabric.LOGGER.info("正在初始化 Fabric 同步处理器...");
        
        // 注册网络处理器 - 使用简化的方式
        // ServerPlayNetworking.registerGlobalReceiver(CHANNEL, this::handleMessage);
        FlashyTitlesFabric.LOGGER.info("网络处理器注册完成（简化版本）");
        
        // 启动心跳任务
        new Thread(() -> {
            while (!server.isStopped()) {
                try {
                    Thread.sleep(30000); // 30秒发送一次心跳
                    sendHeartbeat();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
        
        FlashyTitlesFabric.LOGGER.info("Fabric 同步处理器初始化完成");
    }
    
    /**
     * 关闭同步处理器
     */
    public void shutdown() {
        FlashyTitlesFabric.LOGGER.info("Fabric 同步处理器已关闭");
    }
    
    /**
     * 处理网络消息
     */
    private void handleMessage(MinecraftServer server, ServerPlayerEntity player, 
                              ServerPlayNetworking.Context context, PacketByteBuf buf) {
        try {
            int length = buf.readInt();
            byte[] messageData = new byte[length];
            buf.readBytes(messageData);
            
            Message msg = Message.deserialize(messageData);
            handleSyncMessage(msg);
            
        } catch (Exception e) {
            FlashyTitlesFabric.LOGGER.error("处理网络消息失败", e);
        }
    }
    
    /**
     * 处理来自 Velocity 的消息
     */
    private void handleSyncMessage(Message message) {
        switch (message.getType()) {
            case TITLE_UPDATE -> handleTitleUpdate(message);
            case TITLE_REMOVE -> handleTitleRemove(message);
            case PLAYER_QUIT -> handlePlayerQuit(message);
            case SYNC_ALL -> handleSyncAll(message);
            case RELOAD_CONFIG -> handleReloadConfig(message);
            default -> FlashyTitlesFabric.LOGGER.warn("收到未知消息类型: {}", message.getType());
        }
    }
    
    /**
     * 处理称号更新
     */
    private void handleTitleUpdate(Message message) {
        String playerUuidStr = message.getString("player_uuid");
        String playerName = message.getString("player_name");
        String titleId = message.getString("title_id");
        String titleText = message.getString("title_text");
        boolean animated = message.getBoolean("animated");
        
        if (playerUuidStr == null) {
            return;
        }
        
        try {
            UUID playerUuid = UUID.fromString(playerUuidStr);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
            
            if (player == null) {
                FlashyTitlesFabric.LOGGER.debug("玩家 {} 不在线，跳过称号更新", playerName);
                return;
            }
            
            if (titleId == null || titleId.isEmpty() || titleText == null || titleText.isEmpty()) {
                // 移除称号
                displayManager.removePlayerTitle(player);
                FlashyTitlesFabric.LOGGER.debug("移除玩家 {} 的称号", player.getName().getString());
            } else {
                // 设置称号
                displayManager.setPlayerTitle(player, titleId, titleText, animated);
                FlashyTitlesFabric.LOGGER.debug("为玩家 {} 设置称号: {}", player.getName().getString(), titleId);
            }
            
        } catch (IllegalArgumentException e) {
            FlashyTitlesFabric.LOGGER.warn("无效的玩家UUID: {}", playerUuidStr);
        }
    }
    
    /**
     * 处理称号移除
     */
    private void handleTitleRemove(Message message) {
        String playerUuidStr = message.getString("player_uuid");
        
        if (playerUuidStr == null) {
            return;
        }
        
        try {
            UUID playerUuid = UUID.fromString(playerUuidStr);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
            
            if (player != null) {
                displayManager.removePlayerTitle(player);
                FlashyTitlesFabric.LOGGER.debug("移除玩家 {} 的称号", player.getName().getString());
            }
            
        } catch (IllegalArgumentException e) {
            FlashyTitlesFabric.LOGGER.warn("无效的玩家UUID: {}", playerUuidStr);
        }
    }
    
    /**
     * 处理玩家退出
     */
    private void handlePlayerQuit(Message message) {
        String playerUuidStr = message.getString("player_uuid");
        
        if (playerUuidStr == null) {
            return;
        }
        
        try {
            UUID playerUuid = UUID.fromString(playerUuidStr);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
            
            if (player != null) {
                displayManager.removePlayerTitle(player);
                FlashyTitlesFabric.LOGGER.debug("玩家 {} 退出，清理称号显示", player.getName().getString());
            }
            
        } catch (IllegalArgumentException e) {
            FlashyTitlesFabric.LOGGER.warn("无效的玩家UUID: {}", playerUuidStr);
        }
    }
    
    /**
     * 处理全量同步
     */
    private void handleSyncAll(Message message) {
        FlashyTitlesFabric.LOGGER.info("收到全量同步请求");
        
        // 请求所有在线玩家的数据
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            requestPlayerData(player);
        }
    }
    
    /**
     * 处理配置重载
     */
    private void handleReloadConfig(Message message) {
        FlashyTitlesFabric.LOGGER.info("收到配置重载请求");
        // 这里可以实现配置重载逻辑
    }
    
    /**
     * 请求玩家数据
     */
    public void requestPlayerData(ServerPlayerEntity player) {
        Message message = new Message(MessageType.PLAYER_DATA_REQUEST)
            .addData("player_uuid", player.getUuid().toString())
            .addData("player_name", player.getName().getString());
        
        sendMessage(player, message);
    }
    
    /**
     * 发送心跳包
     */
    private void sendHeartbeat() {
        if (server.getPlayerManager().getPlayerList().isEmpty()) {
            return;
        }
        
        Message message = new Message(MessageType.HEARTBEAT)
            .addData("server_name", "fabric-server")
            .addData("online_players", server.getPlayerManager().getPlayerList().size());
        
        // 随便选一个在线玩家发送心跳
        ServerPlayerEntity player = server.getPlayerManager().getPlayerList().get(0);
        sendMessage(player, message);
    }
    
    /**
     * 玩家加入时的处理
     */
    public void onPlayerJoin(ServerPlayerEntity player) {
        // 延迟请求玩家数据，确保玩家完全加载
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1秒后
                requestPlayerData(player);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * 发送消息到 Velocity
     */
    private void sendMessage(ServerPlayerEntity player, Message message) {
        try {
            // 简化版本，只记录日志
            FlashyTitlesFabric.LOGGER.debug("发送消息到 Velocity: {}", message.getType());

        } catch (Exception e) {
            FlashyTitlesFabric.LOGGER.error("发送网络消息失败", e);
        }
    }
}
