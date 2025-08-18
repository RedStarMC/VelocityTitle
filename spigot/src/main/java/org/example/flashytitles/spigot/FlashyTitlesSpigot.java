package org.example.flashytitles.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.flashytitles.spigot.listener.PlayerListener;
import org.example.flashytitles.spigot.manager.DisplayManager;
import org.example.flashytitles.spigot.papi.FlashyTitlesExpansion;
import org.example.flashytitles.spigot.sync.SyncHandler;

/**
 * FlashyTitles Spigot 插件主类
 */
public class FlashyTitlesSpigot extends JavaPlugin {

    private DisplayManager displayManager;
    private SyncHandler syncHandler;
    private FlashyTitlesExpansion papiExpansion;
    
    @Override
    public void onEnable() {
        getLogger().info("FlashyTitles Spigot 正在启动...");
        
        try {
            // 初始化显示管理器
            displayManager = new DisplayManager(this);
            displayManager.initialize();
            
            // 初始化同步处理器
            syncHandler = new SyncHandler(this, displayManager);
            syncHandler.initialize();
            
            // 注册事件监听器
            getServer().getPluginManager().registerEvents(new PlayerListener(displayManager, syncHandler), this);
            
            // 注册插件消息通道
            getServer().getMessenger().registerIncomingPluginChannel(this, "flashytitles:sync", syncHandler);
            getServer().getMessenger().registerOutgoingPluginChannel(this, "flashytitles:sync");

            // 注册PAPI扩展
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                papiExpansion = new FlashyTitlesExpansion(this, displayManager);
                papiExpansion.register();
                getLogger().info("PlaceholderAPI 扩展已注册");
            } else {
                getLogger().warning("未找到 PlaceholderAPI，称号显示功能将不可用！");
            }

            getLogger().info("FlashyTitles Spigot 启动完成！");
            
        } catch (Exception e) {
            getLogger().severe("FlashyTitles Spigot 启动失败: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("FlashyTitles Spigot 正在关闭...");

        // 注销PAPI扩展
        if (papiExpansion != null) {
            papiExpansion.unregister();
            getLogger().info("PlaceholderAPI 扩展已注销");
        }

        if (displayManager != null) {
            displayManager.shutdown();
        }

        if (syncHandler != null) {
            syncHandler.shutdown();
        }

        getLogger().info("FlashyTitles Spigot 已关闭！");
    }
    
    public DisplayManager getDisplayManager() {
        return displayManager;
    }
    
    public SyncHandler getSyncHandler() {
        return syncHandler;
    }
}
