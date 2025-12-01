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

package top.redstarmc.plugin.velocitytitle.velocity.command.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;

import static net.kyori.adventure.text.Component.text;

public class EditBuilder implements VelocityTitleCommand {
    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("edit")
                .requires(source
                        -> source.hasPermission("velocitytitle.title*")
                        || source.hasPermission("velocitytitle.title.edit")
                        || source.hasPermission("velocitytitle.admin"))
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.word())
                        .executes(context -> 1)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("display")
                                .executes(context -> 1)
                                .then(BrigadierCommand.requiredArgumentBuilder("display", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = context.getArgument("name", String.class);
                                            String display = context.getArgument("display", String.class);

                                            executeDisplay(context.getSource(), name, display);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(LiteralArgumentBuilder.<CommandSource>literal("description")
                                .executes(context -> 1)
                                .then(BrigadierCommand.requiredArgumentBuilder("description", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String name = context.getArgument("name", String.class);
                                            String description = context.getArgument("description", String.class);

                                            executeDescription(context.getSource(), name, description);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                );
    }

    void executeDisplay(CommandSource source, String name, String display){

    }

    void executeDescription(CommandSource source, String name, String description){

    }

}
