package top.redstarmc.plugin.velocitytitle.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VelocityTitleSpigot extends JavaPlugin {

    private static VelocityTitleSpigot instance;

    private LoggerManager logger;

    private PluginMessageBukkit pluginMessage;

    private ExecutorService messageExecutor;

    @Override
    public void onEnable() {
        instance = this;
        logger = new LoggerManager("[VelocityTitle]", true);

        // 1. 初始化线程池（固定大小为8，避免线程过多）
        messageExecutor = Executors.newFixedThreadPool(
                8,
                r -> new Thread(r, "VelocityTitle-Message-Thread") // 线程命名，便于调试
        );

        // 2. 通过子类 VelocitySide 实例化处理器
        // 入站通道（接收Velocity消息）：server
        // 出站通道（向Velocity发送）：proxy
        pluginMessage = new PluginMessageBukkit(messageExecutor, this);


        String[] messageParts = {
                "SendPrivateRaw",
                "[跨服] "
        };

        Future<?> sendFuture = pluginMessage.sendMessage(Bukkit.getServer(), messageParts);

        logger.info("发送");
        logger.info("====================================");
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
}
