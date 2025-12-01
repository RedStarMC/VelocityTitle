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
import top.redstarmc.plugin.velocitytitle.velocity.util.FormatConversion;

import static net.kyori.adventure.text.Component.text;

public class PickBuilder implements VelocityTitleCommand{
    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("pick")
                .executes(context -> {

                    execute(context.getSource());

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
                            String player = context.getArgument("player", String.class);

                            execute(context.getSource(), player);

                            return 1;
                        })
                );
    }

    private void execute(CommandSource source){
        String player = FormatConversion.sourceToPlayer(source).getUsername();

        if (player == null) return;

        execute(source, player);
    }

    private void execute(CommandSource source, String player){

    }

}
