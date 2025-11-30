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

package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

public class ReloadBuilder implements VelocityTitleCommand {

    //TODO 暂不确定是否能正常运行
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("reload")
                .requires(source -> source.hasPermission("velocitytitle.reload"))
                .executes(context -> {

                    VelocityTitleVelocity.getInstance().onProxyReload(new ProxyReloadEvent());

                    context.getSource().sendMessage(TextSer.legToCom(
                            language.getConfigToml().getString("logs.reload")
                    ));

                    return Command.SINGLE_SUCCESS;
                });
    }

}
