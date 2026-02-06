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

package top.redstarmc.plugin.velocitytitle.velocity.command.player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

/**
 * 摘除佩戴的称号
 */
public class PickBuilder implements VelocityTitleCommand {
    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("pick")
                .executes(context -> {

                    if (context.getSource() instanceof Player player) {
                        execute(context.getSource(), player.getUniqueId().toString(), "all");
                    } else {
                        context.getSource().sendMessage(text("仅允许玩家摘除自己的称号"));
                    }

                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("prefix", VelocityBrigadierMessage.tooltip(text("prefix")));
                            builder.suggest("suffix", VelocityBrigadierMessage.tooltip(text("suffix")));
                            builder.suggest("all", VelocityBrigadierMessage.tooltip(text("all")));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String type = context.getArgument("type", String.class);

                            if (context.getSource() instanceof Player player) {
                                execute(context.getSource(), player.getUniqueId().toString(), type);
                            } else {
                                context.getSource().sendMessage(text("仅允许玩家摘除自己的称号"));
                            }

                            return 1;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.string())
                                .requires(source
                                        -> source.hasPermission("velocitytitle.pick.other")
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
                                    String player_name = context.getArgument("player", String.class);
                                    String type = context.getArgument("type", String.class);

                                    DataBaseOperate.selectPlayerUUID(context.getSource(), player_name)
                                            .thenCompose(uuid -> {
                                                execute(context.getSource(), uuid, type);
                                                return CompletableFuture.completedFuture(true);
                                            });

                                    return 1;
                                })
                        )
                );
    }

    private void execute(CommandSource source, String player_uuid, @NotNull String type) {

        switch (type) {
            case "all":
                DataBaseOperate.playerPickTitle(source, player_uuid, true);
                DataBaseOperate.playerPickTitle(source, player_uuid, false);
            case "prefix":
                DataBaseOperate.playerPickTitle(source, player_uuid, true);
            case "suffix":
                DataBaseOperate.playerPickTitle(source, player_uuid, false);
            default:
                source.sendMessage(text("您输入的参数错误！(类型必须是 all、prefix、suffix 中的一个)"));
        }

    }

}
