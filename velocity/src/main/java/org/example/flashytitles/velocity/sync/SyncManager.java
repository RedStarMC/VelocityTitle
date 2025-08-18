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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 同步管理器
 * 负责 Velocity 和 Spigot 服务器之间的数据同步
 */
public class SyncManager {
    
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("flashytitles", "sync");
    
    private final ConfigManager configManager;
    private final TitleManager titleManager;
    private final ProxyServer server;
    private final Logger logger;
    
    private JedisPool jedisPool;
    private ScheduledExecutorService scheduler;
    private boolean redisEnabled;
    
    public SyncManager(ConfigManager configManager, TitleManager titleManager, ProxyServer server, Logger logger) {
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
        
        // 初始化 Redis（如果启用）
        redisEnabled = configManager.isRedisEnabled();
        if (redisEnabled) {
            initializeRedis();
        }
        
        // 启动定时同步任务
        if (configManager.isSyncEnabled()) {
            startSyncTask();
        }
        
        logger.info("同步管理器初始化完成");
        logger.info("- Redis 同步: {}", redisEnabled ? "启用" : "禁用");
        logger.info("- 定时同步: {}", configManager.isSyncEnabled() ? "启用" : "禁用");
    }
    
    /**
     * 初始化 Redis 连接
     */
    private void initializeRedis() {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            
            String password = configManager.getRedisPassword();
            if (password.isEmpty()) {
                password = null;
            }
            
            jedisPool = new JedisPool(
                poolConfig,
                configManager.getRedisHost(),
                configManager.getRedisPort(),
                2000,
                password,
                configManager.getRedisDatabase()
            );
            
            // 测试连接
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.ping();
                logger.info("Redis 连接成功");
            }
            
            // 启动 Redis 订阅
            startRedisSubscription();
            
        } catch (Exception e) {
            logger.error("Redis 初始化失败，将禁用 Redis 同步", e);
            redisEnabled = false;
            if (jedisPool != null) {
                jedisPool.close();
                jedisPool = null;
            }
        }
    }
    
    /**
     * 启动 Redis 订阅
     */
    private void startRedisSubscription() {
        if (!redisEnabled || jedisPool == null) return;
        
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if ("flashytitles:sync".equals(channel)) {
                            handleRedisMessage(message);
                        }
                    }
                }, "flashytitles:sync");
            } catch (Exception e) {
                logger.error("Redis 订阅异常", e);
            }
        });
    }
    
    /**
     * 处理 Redis 消息
     */
    private void handleRedisMessage(String messageStr) {
        try {
            Message message = Message.deserialize(messageStr.getBytes());
            handleSyncMessage(message);
        } catch (Exception e) {
            logger.error("处理 Redis 消息失败", e);
        }
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
        
        // 发送到所有服务器
        broadcastMessage(message);
        
        // 发送到 Redis
        if (redisEnabled) {
            publishToRedis(message);
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
     * 广播消息到所有服务器
     */
    public void broadcastMessage(Message message) {
        byte[] data = createPluginMessage(message);
        
        for (Player player : server.getAllPlayers()) {
            Optional<ServerConnection> serverConnection = player.getCurrentServer();
            if (serverConnection.isPresent()) {
                serverConnection.get().sendPluginMessage(CHANNEL, data);
            }
        }
    }
    
    /**
     * 发送消息到指定服务器
     */
    public void sendMessageToServer(ServerConnection serverConnection, Message message) {
        byte[] data = createPluginMessage(message);
        serverConnection.sendPluginMessage(CHANNEL, data);
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
            case PLAYER_DATA_REQUEST -> {
                String playerUuidStr = message.getString("player_uuid");
                if (playerUuidStr != null) {
                    UUID playerUuid = UUID.fromString(playerUuidStr);
                    Optional<Player> player = server.getPlayer(playerUuid);
                    if (player.isPresent()) {
                        syncPlayerToAllServers(player.get());
                    }
                }
            }
            case HEARTBEAT -> {
                // 心跳包，可以用于检测连接状态
                logger.debug("收到心跳包");
            }
            default -> logger.debug("收到未处理的消息类型: {}", message.getType());
        }
    }
    
    /**
     * 发布消息到 Redis
     */
    private void publishToRedis(Message message) {
        if (!redisEnabled || jedisPool == null) return;
        
        try (Jedis jedis = jedisPool.getResource()) {
            String messageStr = new String(message.serialize());
            jedis.publish("flashytitles:sync", messageStr);
        } catch (Exception e) {
            logger.error("发布 Redis 消息失败", e);
        }
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
     * 玩家离开时的同步处理
     */
    public void onPlayerQuit(Player player) {
        Message message = new Message(MessageType.PLAYER_QUIT)
            .addData("player_uuid", player.getUniqueId().toString())
            .addData("player_name", player.getUsername());
        
        broadcastMessage(message);
        
        if (redisEnabled) {
            publishToRedis(message);
        }
    }
    
    /**
     * 关闭同步管理器
     */
    public void shutdown() {
        logger.info("正在关闭同步管理器...");
        
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
        
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
        
        logger.info("同步管理器已关闭");
    }
}
