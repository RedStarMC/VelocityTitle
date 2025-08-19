package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class PluginMessage {

    /**
     * 请求称号列表
     */
    public static final MinecraftChannelIdentifier TITLE_LIST = MinecraftChannelIdentifier.from("velocitytitle:list");

    /**
     * 请求玩家的称号列表
     */
    public static final MinecraftChannelIdentifier PLAYER_TITLE = MinecraftChannelIdentifier.from("velocitytitle:player-title");

    /**
     * 请求玩家当前佩戴的称号
     */
    public static final MinecraftChannelIdentifier PLAYER_WEAR = MinecraftChannelIdentifier.from("velocitytitle:player-wear");

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        // 无论来源如何，首先检查标识符是否匹配。
        // 这允许将所有消息设置为 IDENTIFIER as handled，
        // 防止转发任何源自客户端的邮件。
        if (!TITLE_LIST.equals(event.getIdentifier())) {
            return;
        }

        // 将 PluginMessage 标记为已处理，表示内容
        // 不应转发到其原始目的地。
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        // 或者：

        // 将 PluginMessage 标记为已转发，表示内容
        // 应该通过，就好像速度不存在一样。
        //
        // 使用时应格外小心，
        // 因为任何客户端都可以自由发送任何它想要的东西，假装是代理
        // event.setResult(PluginMessageEvent.ForwardResult.forward());

        // 仅当源是后端服务器时才尝试解析数据
        if (!(event.getSource() instanceof ServerConnection backend)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        // 处理数据包数据

        if (in.readUTF().equals("Hello Velocity!")){
            System.out.println("Hello Velocity!");
            System.out.println("===========================================================================");
        }

    }
}
