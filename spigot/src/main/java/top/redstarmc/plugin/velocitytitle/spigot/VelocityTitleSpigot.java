package top.redstarmc.plugin.velocitytitle.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VelocityTitleSpigot extends JavaPlugin {

    private static VelocityTitleSpigot instance;

    private LoggerManager logger;

    private PluginMessageBukkit pluginMessage;

    private ExecutorService messageExecutor;

    @Override
    public void onEnable() {
        instance = this;
        logger = new LoggerManager("[VelocityTitle]", true);

        logger.debug("测试1");

        // 1. 初始化线程池（固定大小为8，避免线程过多）
        messageExecutor = Executors.newFixedThreadPool(
                8,
                r -> new Thread(r, "VelocityTitle-Message-Thread") // 线程命名，便于调试
        );

        logger.debug("测试2");

        getServer().getPluginManager().registerEvents(new Listener(), this);



        pluginMessage = new PluginMessageBukkit(messageExecutor, this);

        logger.debug("测试3");

        if (pluginMessage == null) logger.error("空的！");
    }


    @Override
    public void onDisable() {
        getServer().getLogger().info("ces");
    }


    public static VelocityTitleSpigot getInstance() {
        return instance;
    }

    public LoggerManager getLoggerManager() {
        return logger;
    }

    public PluginMessageBukkit getPluginMessage() {
        return pluginMessage;
    }
}
