package top.redstarmc.plugin.velocitytitle.spigot;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.event.EventHandler;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;

import java.util.concurrent.Future;

public class Listener implements org.bukkit.event.Listener {

    private static final LoggerManager logger = VelocityTitleSpigot.getInstance().getLoggerManager();

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {

        PluginMessageBukkit pluginMessage = VelocityTitleSpigot.getInstance().getPluginMessage();

        if (pluginMessage == null) logger.debug("尼玛真是null");

        // 2. 通过子类 VelocitySide 实例化处理器
        // 入站通道（接收Velocity消息）：server
        // 出站通道（向Velocity发送）：proxy

        String[] messageParts = {
                "SendPrivateRaw",
                "[跨服] "
        };

        logger.debug("发送前提示");

        Future<?> sendFuture = pluginMessage.sendMessage(event.getPlayer(), messageParts);

        logger.info("发送");
        logger.info("====================================");

    }

}
