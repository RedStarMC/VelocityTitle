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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;
import top.redstarmc.plugin.velocitytitle.velocity.pojo.Title;

/**
 * 称号操作-列出称号
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
                .requires(source
                        -> source.hasPermission("velocitytitle.title*")
                        || source.hasPermission("velocitytitle.title.list")
                        || source.hasPermission("velocitytitle.admin")
                )
                .executes(context -> {

                    execute(context.getSource());

                    return 1;
                });
    }

    void execute(CommandSource source){

        DataBaseOperate.selectTitleList(source)
                .thenAcceptAsync(titles -> {
                    TextComponent.Builder builder = Component.text()
                            .append(Component.text("§a全局称号列表\n"));

                    for ( Title title : titles ) {
                        builder.append(Component.text(
                                String.format("§7[§f%s§7] §eName: §f%s §7| §eDisplay: §f%s §7| §eDescription: §f%s\n",
                                        title.type().get(), title.name(), title.display(), title.description())
                        ));
                    }

                    source.sendMessage(builder.build());
                });

    }

}
