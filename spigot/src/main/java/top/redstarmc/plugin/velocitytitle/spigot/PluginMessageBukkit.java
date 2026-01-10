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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.util.NetWorkReader;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PluginMessageBukkit implements PluginMessageListener{
    private final String incomingChannel;
    private final String outgoingChannel;
    private final ExecutorService executor;
    private final VelocityTitleSpigot plugin;
    private final LoggerManager log;

    public PluginMessageBukkit(ExecutorService executor, VelocityTitleSpigot server, LoggerManager loggerManager) {
        this.plugin = server;
        this.incomingChannel = "velocitytitle:server";
        this.outgoingChannel = "velocitytitle:proxy";
        this.executor = executor;
        this.log = loggerManager;
        registerChannels();
    }

    private void registerChannels() {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, outgoingChannel)){
            Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, outgoingChannel);
        }
        if (!Bukkit.getMessenger().isIncomingChannelRegistered(plugin, incomingChannel)){
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, incomingChannel , this);
        }
    }

    // 发送消息到代理
    public Future<?> sendMessage(PluginMessageRecipient recipient, String... data) {
        return executor.submit(() -> {
            try {
                byte[][] messages = NetWorkReader.buildMessage(data);
                for (byte[] msg : messages) {
                    recipient.sendPluginMessage(plugin, outgoingChannel, msg);
                }
            } catch (IOException e) {
                log.crash(e, "发送消息到代理时出错");
            }
        });
    }

    // 处理接收到的消息
    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        log.debug("接收到插件消息，开始处理");

        if (!channel.equals(incomingChannel)) {
            log.debug("插件消息【安全过滤1】已触发");
            return;
        }

        if (!player.getServer().getMessenger().isIncomingChannelRegistered(plugin, channel)) {
            log.debug("插件消息【安全过滤2】已触发");
            return;
        }

        try {
            log.debug("开始解码插件消息");
            NetWorkReader reader = NetWorkReader.read(message);
            if (reader.isCompleted()) {
                execute(reader.build());
            }
        } catch (IOException e) {
            log.crash(e, "解码插件消息时出错");
        }

    }

    // 消息执行逻辑
    public void execute(String[] data) {

        log.debug("接收到的插件消息", "==========", Arrays.toString(data), "==========");

//        switch (data[0]) {
//            case "SendPrivateRaw":
//                // 处理跨服私聊
//                String to = data[1];
//                String from = data[2];
//                String rawJson = data[3];
//                String fallback = data.length > 4 ? data[4] : "";
//                Component message = Components.parseRaw(rawJson);
//                Player target = Bukkit.getPlayer(to);
//                if (target != null) {
//                    target.sendMessage(message);
//                }
//                break;
//            case "BroadcastRaw":
//                // 处理代理转发的广播
//                String uuid = data[1];
//                String raw = data[2];
//                String perm = data[3];
//                List<Integer> ports = data[5].isEmpty() ? List.of() :
//                        Arrays.stream(data[5].split(";")).map(Integer::parseInt).toList();
//                Component broadcastMsg = Components.parseRaw(raw);
//                // 只处理本服端口符合条件的消息
//                if (ports.isEmpty() || ports.contains(BukkitProxyManager.getPort())) {
//                    Bukkit.getOnlinePlayers().stream()
//                            .filter(p -> perm.isEmpty() || p.hasPermission(perm))
//                            .forEach(p -> p.sendMessage(UUID.fromString(uuid), broadcastMsg));
//                }
//                break;
//            case "GlobalMute":
//                // 处理全局禁言
//                TrChatBukkit.setGlobalMuting("on".equals(data[1]));
//                break;
//        }
    }


}
