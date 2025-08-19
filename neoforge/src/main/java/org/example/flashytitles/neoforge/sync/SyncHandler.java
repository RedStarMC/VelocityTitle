package org.example.flashytitles.neoforge.sync;

import org.example.flashytitles.core.message.Message;
import org.example.flashytitles.core.message.MessageType;
import org.example.flashytitles.neoforge.FlashyTitlesNeoForge;
import org.example.flashytitles.neoforge.manager.DisplayManager;

import java.util.UUID;

/**
 * NeoForge 同步处理器
 * 处理与 Velocity 的通信
 * 简化版本
 */
public class SyncHandler {

    private final Object server;
    private final DisplayManager displayManager;

    public SyncHandler(Object server, DisplayManager displayManager) {
        this.server = server;
        this.displayManager = displayManager;
    }
    
    /**
     * 初始化同步处理器
     */
    public void initialize() {
        FlashyTitlesNeoForge.LOGGER.info("正在初始化 NeoForge 同步处理器...");

        // 启动心跳任务
        new Thread(() -> {
            while (true) { // 简化的循环条件
                try {
                    Thread.sleep(30000); // 30秒发送一次心跳
                    sendHeartbeat();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        FlashyTitlesNeoForge.LOGGER.info("NeoForge 同步处理器初始化完成");
    }
    
    /**
     * 关闭同步处理器
     */
    public void shutdown() {
        FlashyTitlesNeoForge.LOGGER.info("NeoForge 同步处理器已关闭");
    }
    
    /**
     * 处理同步消息（简化版本）
     */
    public void handleMessage(byte[] data) {
        try {
            Message message = Message.deserialize(data);
            handleSyncMessage(message);
        } catch (Exception e) {
            FlashyTitlesNeoForge.LOGGER.error("处理网络消息失败", e);
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
            default -> FlashyTitlesNeoForge.LOGGER.warn("收到未知消息类型: {}", message.getType());
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
            // 简化版本，使用玩家名作为对象
            Object player = playerName; // 简化的玩家对象

            if (titleId == null || titleId.isEmpty() || titleText == null || titleText.isEmpty()) {
                // 移除称号
                displayManager.removePlayerTitle(player);
                FlashyTitlesNeoForge.LOGGER.debug("移除玩家 {} 的称号", playerName);
            } else {
                // 设置称号
                displayManager.setPlayerTitle(player, titleId, titleText, animated);
                FlashyTitlesNeoForge.LOGGER.debug("为玩家 {} 设置称号: {}", playerName, titleId);
            }
            
        } catch (IllegalArgumentException e) {
            FlashyTitlesNeoForge.LOGGER.warn("无效的玩家UUID: {}", playerUuidStr);
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
            Object player = "Player_" + playerUuid; // 简化的玩家对象

            displayManager.removePlayerTitle(player);
            FlashyTitlesNeoForge.LOGGER.debug("移除玩家的称号");
            
        } catch (IllegalArgumentException e) {
            FlashyTitlesNeoForge.LOGGER.warn("无效的玩家UUID: {}", playerUuidStr);
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
            Object player = "Player_" + playerUuid; // 简化的玩家对象

            displayManager.removePlayerTitle(player);
            FlashyTitlesNeoForge.LOGGER.debug("玩家退出，清理称号显示");
            
        } catch (IllegalArgumentException e) {
            FlashyTitlesNeoForge.LOGGER.warn("无效的玩家UUID: {}", playerUuidStr);
        }
    }
    
    /**
     * 处理全量同步
     */
    private void handleSyncAll(Message message) {
        FlashyTitlesNeoForge.LOGGER.info("收到全量同步请求");
        
        // 简化版本，只记录日志
        FlashyTitlesNeoForge.LOGGER.info("执行全量同步");
    }
    
    /**
     * 处理配置重载
     */
    private void handleReloadConfig(Message message) {
        FlashyTitlesNeoForge.LOGGER.info("收到配置重载请求");
        // 这里可以实现配置重载逻辑
    }
    
    /**
     * 请求玩家数据
     */
    public void requestPlayerData(Object player) {
        String playerName = player.toString();
        Message message = new Message(MessageType.PLAYER_DATA_REQUEST)
            .addData("player_uuid", UUID.nameUUIDFromBytes(playerName.getBytes()).toString())
            .addData("player_name", playerName);

        sendMessage(player, message);
    }
    
    /**
     * 发送心跳包
     */
    private void sendHeartbeat() {
        Message message = new Message(MessageType.HEARTBEAT)
            .addData("server_name", "neoforge-server")
            .addData("online_players", 1); // 简化版本

        // 简化版本，只记录日志
        FlashyTitlesNeoForge.LOGGER.debug("发送心跳包");
    }
    
    /**
     * 玩家加入时的处理
     */
    public void onPlayerJoin(Object player) {
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
    private void sendMessage(Object player, Message message) {
        try {
            // 简化版本，只记录日志
            FlashyTitlesNeoForge.LOGGER.debug("发送消息到 Velocity: {}", message.getType());

        } catch (Exception e) {
            FlashyTitlesNeoForge.LOGGER.error("发送网络消息失败", e);
        }
    }
}
