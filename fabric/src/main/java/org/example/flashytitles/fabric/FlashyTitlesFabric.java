package org.example.flashytitles.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.example.flashytitles.fabric.manager.DisplayManager;
import org.example.flashytitles.fabric.placeholder.FlashyTitlesPlaceholders;
import org.example.flashytitles.fabric.sync.SyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlashyTitles Fabric 模组主类
 */
public class FlashyTitlesFabric implements ModInitializer {
    
    public static final String MOD_ID = "flashy-titles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static DisplayManager displayManager;
    private static SyncHandler syncHandler;
    private static FlashyTitlesPlaceholders placeholders;
    
    @Override
    public void onInitialize() {
        LOGGER.info("FlashyTitles Fabric 正在启动...");
        
        // 注册服务器生命周期事件
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            try {
                // 初始化显示管理器
                displayManager = new DisplayManager(server);
                displayManager.initialize();
                
                // 初始化同步处理器
                syncHandler = new SyncHandler(server, displayManager);
                syncHandler.initialize();

                // 注册Text Placeholder API占位符
                placeholders = new FlashyTitlesPlaceholders(displayManager);
                placeholders.register();
                LOGGER.info("Text Placeholder API 占位符已注册");

                LOGGER.info("FlashyTitles Fabric 启动完成！");
                
            } catch (Exception e) {
                LOGGER.error("FlashyTitles Fabric 启动失败", e);
            }
        });
        
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("FlashyTitles Fabric 正在关闭...");

            // 注销Text Placeholder API占位符
            if (placeholders != null) {
                placeholders.unregister();
                LOGGER.info("Text Placeholder API 占位符已注销");
            }

            if (displayManager != null) {
                displayManager.shutdown();
            }

            if (syncHandler != null) {
                syncHandler.shutdown();
            }

            LOGGER.info("FlashyTitles Fabric 已关闭！");
        });
        
        // 注册玩家连接事件
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (syncHandler != null) {
                syncHandler.onPlayerJoin(handler.getPlayer());
            }
        });
        
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (displayManager != null) {
                displayManager.removePlayerTitle(handler.getPlayer());
            }
        });
    }
    
    public static DisplayManager getDisplayManager() {
        return displayManager;
    }
    
    public static SyncHandler getSyncHandler() {
        return syncHandler;
    }
}
