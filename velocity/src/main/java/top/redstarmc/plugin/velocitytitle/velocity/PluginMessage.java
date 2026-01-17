/*
 * This file is part of VelocityTitle(https://github.com/RedStarMC/VelocityTitle).
 *
 * Copyright (C) RedStarMC and contributors.
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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.util.NetWorkReader;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

import java.io.IOException;

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

    public static void sendMessage(@NotNull Player player, String... d){
        try {
            byte[][] data = NetWorkReader.buildMessage(d);

            player.getCurrentServer().ifPresentOrElse
                    (serverConnection -> {
                        for (byte[] msg : data){
                            serverConnection.sendPluginMessage(OUTGOING, msg);
                        }
                    }, () -> logger.warn("服务器连接为空！"));

        } catch (IOException e) {
            logger.crash(e, "发送插件消息失败");
        }
    }
}
