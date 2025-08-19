package org.example.flashytitles.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.example.flashytitles.velocity.command.TitleCommand;
import org.example.flashytitles.velocity.config.ConfigManager;
import org.example.flashytitles.velocity.listener.PlayerListener;
import org.example.flashytitles.velocity.manager.TitleManager;
import org.example.flashytitles.velocity.sync.SimplifiedSyncManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "flashy-titles",
    name = "FlashyTitles",
    version = "@VERSION@",
    description = "群组服务器称号同步系统 - 支持动态效果和跨服同步",
    authors = {"maple"}
)
public class FlashyTitlesVelocity {

    private static FlashyTitlesVelocity instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    
    private ConfigManager configManager;
    private TitleManager titleManager;
    private SimplifiedSyncManager syncManager;
    
    @Inject
    public FlashyTitlesVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("FlashyTitles 正在启动...");
        
        try {
            // 初始化配置管理器
            configManager = new ConfigManager(dataDirectory, logger);
            configManager.loadConfig();
            
            // 初始化称号管理器
            titleManager = new TitleManager(configManager, logger);
            titleManager.initialize();
            
            // 初始化同步管理器
            syncManager = new SimplifiedSyncManager(configManager, titleManager, server, logger);
            syncManager.initialize();

            // 设置同步管理器到称号管理器（用于权限检查）
            titleManager.setSyncManager(syncManager);
            
            // 注册命令
            server.getCommandManager().register("title", new TitleCommand(titleManager, server, logger));
            
            // 注册事件监听器
            server.getEventManager().register(this, new PlayerListener(titleManager, syncManager, logger));
            
            logger.info("FlashyTitles 启动完成！");
            logger.info("- 数据库类型: {}", configManager.getDatabaseType());
            logger.info("- 同步模式: {}", configManager.isSyncEnabled() ? "启用" : "禁用");
            logger.info("- 已加载称号数量: {}", titleManager.getAllTitles().size());
            
        } catch (Exception e) {
            logger.error("FlashyTitles 启动失败: ", e);
        }
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("FlashyTitles 正在关闭...");
        
        if (syncManager != null) {
            syncManager.shutdown();
        }
        
        if (titleManager != null) {
            titleManager.shutdown();
        }
        
        logger.info("FlashyTitles 已关闭！");
    }
    
    // Static instance getter
    public static FlashyTitlesVelocity getInstance() {
        return instance;
    }

    // Getters for other classes
    public ProxyServer getServer() { return server; }
    public Logger getLogger() { return logger; }
    public ConfigManager getConfigManager() { return configManager; }
    public TitleManager getTitleManager() { return titleManager; }
    public SimplifiedSyncManager getSyncManager() { return syncManager; }
}
