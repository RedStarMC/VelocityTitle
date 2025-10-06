package org.example.flashytitles.spigot.sync;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.example.flashytitles.core.message.Message;
import org.example.flashytitles.core.message.MessageType;
import org.example.flashytitles.spigot.manager.DisplayManager;

import java.util.UUID;

/**
 * 同步处理器
 * 处理与 Velocity 的通信
 */
public class SyncHandler implements PluginMessageListener {
    
    private final JavaPlugin plugin;
    private final DisplayManager displayManager;
    
    public SyncHandler(JavaPlugin plugin, DisplayManager displayManager) {
        this.plugin = plugin;
        this.displayManager = displayManager;
    }
    
    /**
     * 初始化同步处理器
     */
    public void initialize() {
        plugin.getLogger().info("正在初始化同步处理器...");
        
        // 启动心跳任务
        Bukkit.getScheduler().runTaskTimer(plugin, this::sendHeartbeat, 0L, 20L * 30L); // 每30秒发送心跳
        
        plugin.getLogger().info("同步处理器初始化完成");
    }
    
    /**
     * 关闭同步处理器
     */
    public void shutdown() {
        plugin.getLogger().info("同步处理器已关闭");
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!"flashytitles:sync".equals(channel)) {
            return;
        }
        
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            int length = in.readInt();
            byte[] messageData = new byte[length];
            in.readFully(messageData);
            
            Message msg = Message.deserialize(messageData);
            handleMessage(msg);
            
        } catch (Exception e) {
            plugin.getLogger().severe("处理插件消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 处理来自 Velocity 的消息
     */
    private void handleMessage(Message message) {
        switch (message.getType()) {
            case TITLE_UPDATE -> handleTitleUpdate(message);
            case TITLE_REMOVE -> handleTitleRemove(message);
            case PLAYER_QUIT -> handlePlayerQuit(message);
            case SYNC_ALL -> handleSyncAll(message);
            case RELOAD_CONFIG -> handleReloadConfig(message);
            case PERMISSION_CHECK -> handlePermissionCheck(message);
            default -> plugin.getLogger().warning("收到未知消息类型: " + message.getType());
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
            Player player = Bukkit.getPlayer(playerUuid);
            
            if (player == null || !player.isOnline()) {
                plugin.getLogger().info("玩家 " + playerName + " 不在线，跳过称号更新");
                return;
            }
            
            if (titleId == null || titleId.isEmpty() || titleText == null || titleText.isEmpty()) {
                // 移除称号
                displayManager.removePlayerTitle(player);
                plugin.getLogger().info("移除玩家 " + player.getName() + " 的称号");
            } else {
                // 设置称号
                displayManager.setPlayerTitle(player, titleId, titleText, animated);
                plugin.getLogger().info("为玩家 " + player.getName() + " 设置称号: " + titleId);
            }
            
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("无效的玩家UUID: " + playerUuidStr);
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
            Player player = Bukkit.getPlayer(playerUuid);
            
            if (player != null && player.isOnline()) {
                displayManager.removePlayerTitle(player);
                plugin.getLogger().info("移除玩家 " + player.getName() + " 的称号");
            }
            
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("无效的玩家UUID: " + playerUuidStr);
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
            Player player = Bukkit.getPlayer(playerUuid);
            
            if (player != null) {
                displayManager.removePlayerTitle(player);
                plugin.getLogger().info("玩家 " + player.getName() + " 退出，清理称号显示");
            }
            
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("无效的玩家UUID: " + playerUuidStr);
        }
    }
    
    /**
     * 处理全量同步
     */
    private void handleSyncAll(Message message) {
        plugin.getLogger().info("收到全量同步请求");
        
        // 请求所有在线玩家的数据
        for (Player player : Bukkit.getOnlinePlayers()) {
            requestPlayerData(player);
        }
    }
    
    /**
     * 处理配置重载
     */
    private void handleReloadConfig(Message message) {
        plugin.getLogger().info("收到配置重载请求");
        // 这里可以实现配置重载逻辑
    }

    /**
     * 处理权限检查请求
     */
    private void handlePermissionCheck(Message message) {
        String playerUuidStr = message.getString("player_uuid");
        String playerName = message.getString("player_name");
        String permission = message.getString("permission");
        String requestId = message.getString("request_id");

        if (playerUuidStr == null || permission == null || requestId == null) {
            plugin.getLogger().warning("权限检查请求参数不完整");
            return;
        }

        try {
            UUID playerUuid = UUID.fromString(playerUuidStr);
            Player player = Bukkit.getPlayer(playerUuid);

            boolean hasPermission = false;
            if (player != null && player.isOnline()) {
                // 检查玩家权限
                hasPermission = player.hasPermission(permission);
                plugin.getLogger().info("权限检查: 玩家 " + player.getName() + " 权限 " + permission + " = " + hasPermission);
            } else {
                plugin.getLogger().info("玩家 " + playerName + " 不在线，权限检查失败");
            }

            // 发送权限检查响应
            Message response = new Message(MessageType.PERMISSION_RESPONSE)
                .addData("request_id", requestId)
                .addData("player_uuid", playerUuidStr)
                .addData("player_name", playerName)
                .addData("permission", permission)
                .addData("has_permission", hasPermission);

            if (player != null) {
                sendMessage(player, response);
            }

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("无效的玩家UUID: " + playerUuidStr);
        }
    }
    
    /**
     * 请求玩家数据
     */
    public void requestPlayerData(Player player) {
        Message message = new Message(MessageType.PLAYER_DATA_REQUEST)
            .addData("player_uuid", player.getUniqueId().toString())
            .addData("player_name", player.getName());
        
        sendMessage(player, message);
    }
    
    /**
     * 发送心跳包
     */
    private void sendHeartbeat() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        
        Message message = new Message(MessageType.HEARTBEAT)
            .addData("server_name", plugin.getServer().getName())
            .addData("online_players", Bukkit.getOnlinePlayers().size());
        
        // 随便选一个在线玩家发送心跳
        Player player = Bukkit.getOnlinePlayers().iterator().next();
        sendMessage(player, message);
    }
    
    /**
     * 发送消息到 Velocity
     */
    private void sendMessage(Player player, Message message) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            byte[] messageData = message.serialize();
            out.writeInt(messageData.length);
            out.write(messageData);
            
            player.sendPluginMessage(plugin, "flashytitles:sync", out.toByteArray());
            
        } catch (Exception e) {
            plugin.getLogger().severe("发送插件消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
