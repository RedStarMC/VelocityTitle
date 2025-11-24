package top.redstarmc.plugin.velocitytitle.velocity;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

public class PluginMessage {

    /**
     * 接收子服消息的通道
     */
    public static final MinecraftChannelIdentifier INCOMING = MinecraftChannelIdentifier.from("velocitytitle:proxy");

    /**
     * 向子服发送消息的通道
     */
    public static final MinecraftChannelIdentifier OUTGOING = MinecraftChannelIdentifier.from("velocitytitle:server");

    private static final LoggerManager logger = VelocityTitleVelocity.getInstance().getLogger();


}
