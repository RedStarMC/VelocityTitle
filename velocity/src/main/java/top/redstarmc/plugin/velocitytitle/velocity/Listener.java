/*
 * This file is part of VelocityTitle.
 *
 * Copyright (C) RedStarMC, pingguomc and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

/**
 * <h1>插件监听器</h1>
 */
public class Listener {

    private static final LoggerManager logger = VelocityTitleVelocity.getInstance().getLogger();

    /**
     * 进服时保存UUID，以便离线时查验。<br>
     * 其实是为了适配离线服务器<br>
     * 保存在 PlayerWear 中，以便后端服务器查询称号时找不到人
     * @param event 连接子服事件
     */
    @Subscribe
    public void onServerPostConnectEvent(@NotNull ServerPostConnectEvent event){
        Player player = event.getPlayer();

        // TODO
        // PlayerWearOperate.replaceUUID(EasySQLManager.getSqlManager(), player.getUniqueId().toString(), name);
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {

        logger.debug("接收到插件消息-0");

        // 无论来源如何，首先检查标识符是否匹配。
        // 这允许将所有消息设置为 IDENTIFIER as handled，
        // 防止转发任何源自客户端的邮件。
        if (!event.getIdentifier().equals(PluginMessage.INCOMING)) {
            return;
        }

        logger.debug("接收到插件消息-1");
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
        if (!(event.getSource() instanceof ServerConnection source)) {
            logger.error("Invalid message source (not ServerConnection)");
            return;
        }
        logger.debug("接收到插件消息-2");
//        try {
//            // 解析消息（字符串数组）
//            MessageReader reader = MessageReader.read(event.getData());
//            if (reader.isCompleted()) {
//                String[] data = reader.build();
//                execute(data, source);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        logger.debug(event.toString());

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        // 处理数据包数据

        if (in.readUTF().equals("Hello Velocity!")){
            System.out.println("Hello Velocity!");
            System.out.println("===========================================================================");
        }

    }

}
