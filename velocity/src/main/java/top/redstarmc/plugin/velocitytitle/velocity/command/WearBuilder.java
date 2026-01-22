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

package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.Player;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;

import static net.kyori.adventure.text.Component.text;

/**
 * 玩家穿戴称号
 */
public class WearBuilder implements VelocityTitleCommand{

    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("wear")
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.word())
                        .executes(context -> { // 给自己改
                            String name = context.getArgument("name", String.class);

                            execute(context.getSource(), name);

                            return 1;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.string())
                                .requires(source
                                    -> source.hasPermission("velocitytitle.wear.other")
                                    || source.hasPermission("velocitytitle.admin")
                                )
                                .suggests((context, builder) -> { // 提供所有的玩家名字
                                    proxyServer.getAllPlayers().forEach(player -> builder.suggest(
                                            player.getUsername(),
                                            VelocityBrigadierMessage.tooltip(text(player.getUsername()))
                                    ));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String title_name = context.getArgument("name", String.class);
                                    String player = context.getArgument("player", String.class);

                                    execute(context.getSource(), title_name, player);

                                    return 1;
                                })
                        )
                );
    }

    private void execute(CommandSource source, String title_name, String player_name) {
        Player player = proxyServer.getPlayer(player_name).orElse(null);
        if (player == null) {
            DataBaseOperate.selectPlayerUUID(source, player_name)
                    .thenAccept(uuid -> {
                        DataBaseOperate.wearTitle(source, title_name, uuid);
                    });
        } else {
            DataBaseOperate.wearTitle(source, title_name, player.getUniqueId().toString());
        }
    }

    private void execute(CommandSource source, String title_name) {
        if (source instanceof Player player) {
            String player_uuid = player.getUniqueId().toString();
            DataBaseOperate.wearTitle(source, title_name, player_uuid);
        } else {
            source.sendMessage(text("仅允许玩家操作自己的称号穿戴情况"));
        }
    }
}
