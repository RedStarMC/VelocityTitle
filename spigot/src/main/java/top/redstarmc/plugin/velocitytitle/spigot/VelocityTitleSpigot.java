package top.redstarmc.plugin.velocitytitle.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.plugin.java.JavaPlugin;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;


public class VelocityTitleSpigot extends JavaPlugin {

    private static VelocityTitleSpigot instance;

    private LoggerManager logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = new LoggerManager("[VelocityTitle]", true);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "velocitytitle:list");

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Hello Velocity!");
        getServer().sendPluginMessage(this, "velocitytitle:list", out.toByteArray());

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
