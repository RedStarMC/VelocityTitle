package org.example.flashytitles.fabric.sync;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.example.flashytitles.core.message.Message;
import org.example.flashytitles.core.message.MessageType;
import org.example.flashytitles.fabric.FlashyTitlesFabric;
import org.example.flashytitles.fabric.manager.DisplayManager;
import org.example.flashytitles.fabric.network.TitleSyncPayload;

import java.util.UUID;

/**
 * Fabric 同步处理器
 * 处理与 Velocity 的通信
 */
public class SyncHandler {
    
    // 移除旧的CHANNEL，使用新的CustomPayload系统
    
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

        // 注册CustomPayload
        PayloadTypeRegistry.playS2C().register(TitleSyncPayload.ID, TitleSyncPayload.CODEC);

        // 注册网络处理器
        ServerPlayNetworking.registerGlobalReceiver(TitleSyncPayload.ID, this::handleTitleSync);

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
     * 处理称号同步数据包
     */
    private void handleTitleSync(TitleSyncPayload payload, ServerPlayNetworking.Context context) {
        try {
            ServerPlayerEntity player = context.player();

            switch (payload.action()) {
                case "equip" -> {
                    displayManager.setPlayerTitle(player, payload.titleId(), payload.titleText(), payload.animated());
                    FlashyTitlesFabric.LOGGER.debug("玩家 {} 装备称号: {}", player.getName().getString(), payload.titleId());
                }
                case "unequip" -> {
                    displayManager.removePlayerTitle(player);
                    FlashyTitlesFabric.LOGGER.debug("玩家 {} 取消装备称号", player.getName().getString());
                }
                case "heartbeat" -> {
                    // 心跳包，不需要处理
                }
                default -> FlashyTitlesFabric.LOGGER.warn("收到未知的称号同步动作: {}", payload.action());
            }

        } catch (Exception e) {
            FlashyTitlesFabric.LOGGER.error("处理称号同步数据包失败", e);
        }
    }
    

    

    

    

    

    
    /**
     * 发送心跳包
     */
    private void sendHeartbeat() {
        if (server.getPlayerManager().getPlayerList().isEmpty()) {
            return;
        }

        // 使用新的CustomPayload系统发送心跳
        TitleSyncPayload heartbeat = TitleSyncPayload.heartbeat();

        // 向所有在线玩家发送心跳（实际上这个心跳是发给Velocity的）
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, heartbeat);
            break; // 只需要发送给一个玩家即可
        }
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
     * 请求玩家数据
     */
    private void requestPlayerData(ServerPlayerEntity player) {
        // 使用新的CustomPayload系统请求玩家数据
        // 这里可以发送一个特殊的payload来请求Velocity同步玩家数据
        TitleSyncPayload request = new TitleSyncPayload("request_data", player.getUuidAsString(), "", false);
        ServerPlayNetworking.send(player, request);

        FlashyTitlesFabric.LOGGER.debug("请求玩家 {} 的数据", player.getName().getString());
    }
}
