package org.example.flashytitles.velocity.sync;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.example.flashytitles.core.message.Message;
import org.example.flashytitles.core.message.MessageType;
import org.example.flashytitles.core.model.Title;
import org.example.flashytitles.velocity.config.ConfigManager;
import org.example.flashytitles.velocity.manager.TitleManager;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 简化的同步管理器
 * 基于数据库同步，移除Redis依赖
 */
public class SimplifiedSyncManager {
    
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("flashytitles", "sync");
    
    private final ConfigManager configManager;
    private final TitleManager titleManager;
    private final ProxyServer server;
    private final Logger logger;

    private ScheduledExecutorService scheduler;

    // 权限检查响应缓存
    private final ConcurrentHashMap<String, Boolean> permissionCache = new ConcurrentHashMap<>();
    
    public SimplifiedSyncManager(ConfigManager configManager, TitleManager titleManager, ProxyServer server, Logger logger) {
        this.configManager = configManager;
        this.titleManager = titleManager;
        this.server = server;
        this.logger = logger;
    }
    
    /**
     * 初始化同步管理器
     */
    public void initialize() {
        logger.info("正在初始化同步管理器...");
        
        // 注册插件消息通道
        server.getChannelRegistrar().register(CHANNEL);
        
        // 启动定时同步任务
        if (configManager.isSyncEnabled()) {
            startSyncTask();
        }
        
        logger.info("同步管理器初始化完成 (基于数据库同步)");
        logger.info("- 定时同步: {}", configManager.isSyncEnabled() ? "启用" : "禁用");
    }
    
    /**
     * 启动定时同步任务
     */
    private void startSyncTask() {
        scheduler = Executors.newScheduledThreadPool(1);
        
        int interval = configManager.getSyncInterval();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                syncAllPlayersToServers();
            } catch (Exception e) {
                logger.error("定时同步任务异常", e);
            }
        }, interval, interval, TimeUnit.SECONDS);
        
        logger.info("定时同步任务已启动，间隔: {} 秒", interval);
    }
    
    /**
     * 同步所有玩家数据到所有服务器
     */
    public void syncAllPlayersToServers() {
        for (Player player : server.getAllPlayers()) {
            syncPlayerToAllServers(player);
        }
    }
    
    /**
     * 同步玩家数据到所有服务器
     */
    public void syncPlayerToAllServers(Player player) {
        Optional<ServerConnection> currentServer = player.getCurrentServer();
        if (currentServer.isPresent()) {
            syncPlayerToServer(player, currentServer.get());
        }
    }
    
    /**
     * 同步玩家数据到指定服务器
     */
    public void syncPlayerToServer(Player player, ServerConnection server) {
        String equippedTitle = titleManager.getEquippedTitle(player.getUniqueId());
        
        Message message = new Message(MessageType.TITLE_UPDATE)
            .addData("player_uuid", player.getUniqueId().toString())
            .addData("player_name", player.getUsername());
        
        if (equippedTitle != null) {
            Title title = titleManager.getTitle(equippedTitle);
            if (title != null) {
                String renderedTitle = titleManager.renderTitle(equippedTitle);
                message.addData("title_id", equippedTitle)
                       .addData("title_text", renderedTitle)
                       .addData("animated", title.isAnimated());
            }
        } else {
            message.addData("title_id", "")
                   .addData("title_text", "")
                   .addData("animated", false);
        }
        
        sendMessageToServer(server, message);
    }
    
    /**
     * 发送消息到指定服务器
     */
    private void sendMessageToServer(ServerConnection server, Message message) {
        try {
            byte[] data = createPluginMessage(message);
            server.sendPluginMessage(CHANNEL, data);
        } catch (Exception e) {
            logger.error("发送插件消息失败", e);
        }
    }
    
    /**
     * 创建插件消息数据
     */
    private byte[] createPluginMessage(Message message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        byte[] messageData = message.serialize();
        out.writeInt(messageData.length);
        out.write(messageData);
        return out.toByteArray();
    }
    
    /**
     * 玩家加入时的同步处理
     */
    public void onPlayerJoin(Player player) {
        if (configManager.isAutoSyncOnJoin()) {
            // 延迟一点时间，确保玩家完全加入服务器
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                Optional<ServerConnection> serverConnection = player.getCurrentServer();
                if (serverConnection.isPresent()) {
                    syncPlayerToServer(player, serverConnection.get());
                }
            });
        }
    }
    
    /**
     * 玩家离开时的处理
     */
    public void onPlayerQuit(Player player) {
        // 通知所有服务器玩家离开
        Message message = new Message(MessageType.PLAYER_QUIT)
            .addData("player_uuid", player.getUniqueId().toString())
            .addData("player_name", player.getUsername());
        
        // 广播到所有服务器
        for (Player onlinePlayer : server.getAllPlayers()) {
            if (!onlinePlayer.equals(player)) {
                Optional<ServerConnection> serverConnection = onlinePlayer.getCurrentServer();
                if (serverConnection.isPresent()) {
                    sendMessageToServer(serverConnection.get(), message);
                }
            }
        }
    }
    
    /**
     * 处理来自 Spigot 的消息
     */
    public void handlePluginMessage(Player player, ByteArrayDataInput in) {
        try {
            int length = in.readInt();
            byte[] messageData = new byte[length];
            in.readFully(messageData);

            Message message = Message.deserialize(messageData);
            handleSyncMessage(message);

        } catch (Exception e) {
            logger.error("处理插件消息失败", e);
        }
    }

    /**
     * 处理同步消息
     */
    private void handleSyncMessage(Message message) {
        switch (message.getType()) {
            case PLAYER_DATA_REQUEST -> handlePlayerDataRequest(message);
            case PERMISSION_RESPONSE -> handlePermissionResponse(message);
            case HEARTBEAT -> handleHeartbeat(message);
            default -> logger.debug("收到消息类型: {}", message.getType());
        }
    }

    /**
     * 处理玩家数据请求
     */
    private void handlePlayerDataRequest(Message message) {
        String playerUuidStr = message.getString("player_uuid");
        if (playerUuidStr != null) {
            try {
                UUID playerUuid = UUID.fromString(playerUuidStr);
                Optional<Player> playerOpt = server.getPlayer(playerUuid);
                if (playerOpt.isPresent()) {
                    Player player = playerOpt.get();
                    Optional<ServerConnection> serverConnection = player.getCurrentServer();
                    if (serverConnection.isPresent()) {
                        syncPlayerToServer(player, serverConnection.get());
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.warn("无效的玩家UUID: {}", playerUuidStr);
            }
        }
    }

    /**
     * 处理权限检查响应
     */
    private void handlePermissionResponse(Message message) {
        String requestId = message.getString("request_id");
        String playerUuid = message.getString("player_uuid");
        String permission = message.getString("permission");
        boolean hasPermission = message.getBoolean("has_permission");

        if (requestId != null) {
            // 将权限检查结果存入缓存
            permissionCache.put(requestId, hasPermission);
            logger.debug("收到权限检查响应: 玩家 {} 权限 {} = {}", playerUuid, permission, hasPermission);
        }
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(Message message) {
        String serverName = message.getString("server_name");
        int onlinePlayers = message.getInt("online_players");
        logger.debug("收到来自服务器 {} 的心跳，在线玩家: {}", serverName, onlinePlayers);
    }

    /**
     * 检查玩家权限（异步）
     */
    public CompletableFuture<Boolean> checkPlayerPermission(UUID playerUuid, String permission) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Player> playerOpt = server.getPlayer(playerUuid);
            if (playerOpt.isEmpty()) {
                logger.warn("玩家 {} 不在线，无法检查权限", playerUuid);
                return false;
            }

            Player player = playerOpt.get();
            Optional<ServerConnection> serverConnection = player.getCurrentServer();
            if (serverConnection.isEmpty()) {
                logger.warn("玩家 {} 未连接到任何服务器，无法检查权限", player.getUsername());
                return false;
            }

            // 生成请求ID
            String requestId = UUID.randomUUID().toString();

            // 创建权限检查请求
            Message permissionRequest = new Message(MessageType.PERMISSION_CHECK)
                .addData("player_uuid", playerUuid.toString())
                .addData("player_name", player.getUsername())
                .addData("permission", permission)
                .addData("request_id", requestId);

            // 发送权限检查请求到后端服务器
            sendMessageToServer(serverConnection.get(), permissionRequest);

            // 等待响应（简化版本）
            try {
                int maxWaitTime = 3000; // 最多等待3秒
                int checkInterval = 100; // 每100ms检查一次
                int waitedTime = 0;

                while (waitedTime < maxWaitTime) {
                    Boolean result = permissionCache.get(requestId);
                    if (result != null) {
                        // 清理缓存
                        permissionCache.remove(requestId);
                        logger.debug("权限检查完成: 玩家 {} 权限 {} = {}", player.getUsername(), permission, result);
                        return result;
                    }

                    Thread.sleep(checkInterval);
                    waitedTime += checkInterval;
                }

                // 超时，默认允许（避免阻塞购买流程）
                logger.warn("权限检查超时，默认允许: 玩家 {} 权限 {}", player.getUsername(), permission);
                return true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("权限检查被中断", e);
                return false;
            }
        });
    }

    /**
     * 关闭同步管理器
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logger.info("同步管理器已关闭");
    }
}
