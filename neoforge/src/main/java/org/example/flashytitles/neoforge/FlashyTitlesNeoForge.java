package org.example.flashytitles.neoforge;

import org.example.flashytitles.neoforge.manager.DisplayManager;
import org.example.flashytitles.neoforge.placeholder.PlaceholderService;
import org.example.flashytitles.neoforge.sync.SyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlashyTitles NeoForge 模组主类
 * 简化版本，使用标准 Minecraft 服务器 API
 */
public class FlashyTitlesNeoForge {

    public static final String MOD_ID = "flashy-titles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static DisplayManager displayManager;
    private static SyncHandler syncHandler;
    private static PlaceholderService placeholderService;
    private static boolean initialized = false;

    /**
     * 初始化模组（由服务器启动时调用）
     */
    public static void initialize(Object server) {
        if (initialized) return;

        LOGGER.info("FlashyTitles NeoForge 正在启动...");

        try {
            // 初始化显示管理器
            displayManager = new DisplayManager(server);
            displayManager.initialize();

            // 初始化同步处理器
            syncHandler = new SyncHandler(server, displayManager);
            syncHandler.initialize();

            // 初始化内置占位符服务
            placeholderService = new PlaceholderService(displayManager);
            LOGGER.info("内置占位符服务已初始化");

            initialized = true;
            LOGGER.info("FlashyTitles NeoForge 启动完成！");

        } catch (Exception e) {
            LOGGER.error("FlashyTitles NeoForge 启动失败", e);
        }
    }

    /**
     * 关闭模组
     */
    public static void shutdown() {
        LOGGER.info("FlashyTitles NeoForge 正在关闭...");

        if (displayManager != null) {
            displayManager.shutdown();
        }

        if (syncHandler != null) {
            syncHandler.shutdown();
        }

        initialized = false;
        LOGGER.info("FlashyTitles NeoForge 已关闭！");
    }

    public static DisplayManager getDisplayManager() {
        return displayManager;
    }

    public static SyncHandler getSyncHandler() {
        return syncHandler;
    }

    public static PlaceholderService getPlaceholderService() {
        return placeholderService;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
