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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;
import top.redstarmc.plugin.velocitytitle.velocity.database.Title;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

/**
 * 玩家称号-称号列表
 * 列出玩家自己的称号列表
 */
public class ListBuilder implements VelocityTitleCommand {

    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("list")
                .executes(context -> {

                    if (context.getSource() instanceof Player player) {
                        execute(context.getSource(), player.getUniqueId().toString());
                    } else {
                        context.getSource().sendMessage(text("仅允许玩家查看自己的称号列表"));
                    }

                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((context, builder) -> { // 提供所有的玩家名字
                            proxyServer.getAllPlayers().forEach(player -> builder.suggest(
                                    player.getUsername(),
                                    VelocityBrigadierMessage.tooltip(text(player.getUsername()))
                            ));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String player_name = context.getArgument("player", String.class);

                            DataBaseOperate.selectPlayerUUID(context.getSource(), player_name)
                                    .thenCompose(uuid -> {
                                        execute(context.getSource(), uuid);
                                        return CompletableFuture.completedFuture(true);
                                    });

                            return 1;
                        })
                );
    }

    private void execute(@NotNull CommandSource source, @NotNull String player_uuid) {
        DataBaseOperate.selectPlayerTitleList(source, player_uuid)
                .thenAcceptAsync(titleList -> {
                    TextComponent textComponent = Component.text("你所拥有的称号列表\n");

                    for (Title title : titleList) {
                        textComponent.append(Component.text("Name: " + title.name() + " Display: " + title.display() + " D:" + title.description() + "\n"));
                    }

                    source.sendMessage(textComponent);

                });
    }

}
