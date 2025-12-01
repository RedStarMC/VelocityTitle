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
