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

package top.redstarmc.plugin.velocitytitle.spigot;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import top.redstarmc.plugin.velocitytitle.spigot.manager.ConfigManager;

import static net.kyori.adventure.text.Component.text;

public class CommandBuilder {

    ConfigManager language = VelocityTitleSpigot.getInstance().getLanguage();

    public LiteralArgumentBuilder<CommandSourceStack> init(){
        return LiteralArgumentBuilder.<CommandSourceStack>literal("VelocityTitleSpigot")
                .executes(context -> {
                    context.getSource().getSender().sendMessage(
                            text(language.getConfigToml().getString("command.root"))
                                    .append(text(language.getConfigToml().getString("command.success")))
                    );
                    return 1;
                })
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("gui")
                        .executes(context -> {

                            CommandSourceStack source = context.getSource();

                            if ( !( source instanceof Player player)){
                                source.getSender().sendMessage(text(language.getConfigToml().getString("command.console")));
                                return 1;
                            }

                            player.sendMessage("Sorry");

                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("help")
                        .executes(context -> {
                            context.getSource().getSender().sendMessage(
                                    text(language.getConfigToml().getString("command.root"))
                                            .append(text(language.getConfigToml().getString("command.help")))
                                            .append(text(language.getConfigToml().getString("command.gui")))
                            );
                            return 1;
                        })
                );
    }

}
