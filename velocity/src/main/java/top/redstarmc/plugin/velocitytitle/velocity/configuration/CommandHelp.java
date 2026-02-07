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

package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.api.AbstractTomlManager;
import top.redstarmc.plugin.velocitytitle.core.util.toStrings;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

import java.util.List;

public class CommandHelp {

    private static ConfigManager language;

    private static final String sharp = "&9>&r ";

    @Deprecated
    private static final String next = "\n";

    public static void init(){
        language = VelocityTitleVelocity.getInstance().getLanguage();
    }

    private static String prefix(){
        return language.getConfigToml().getString("commands.prefix");
    }

    private static String head(){
        return toStrings.format(language.getConfigToml().getString("commands.head"), AbstractTomlManager.d_version);
    }


    public static @NotNull List<Component> root(){
        return TextSer.toComponentList(
                prefix() + head(),
                prefix() + language.getConfigToml().getString("commands.help")
        );
    }

    public static @NotNull List<Component> help(){
        return TextSer.toComponentList(
                prefix() + head(),
                sharp + "&a/vt title < create | edit | delete | list | meta >&r",
                sharp + "&a/vt player < divide | revoke | wear | pick | list >&r",
                sharp + "&a/vt reload"
        );
    }

    public static @NotNull List<Component> title(){
        return TextSer.toComponentList(
                prefix() + language.getConfigToml().getString("commands.title"),
                sharp + language.getConfigToml().getString("commands.titles.create"),
                sharp + language.getConfigToml().getString("commands.titles.edit"),
                sharp + language.getConfigToml().getString("commands.titles.delete"),
                sharp + language.getConfigToml().getString("commands.titles.list"),
                sharp + language.getConfigToml().getString("commands.titles.meta")
        );
    }

    public static @NotNull List<Component> player() {
        return TextSer.toComponentList(
                prefix() + language.getConfigToml().getString("commands.player"),
                sharp + language.getConfigToml().getString("commands.players.divide"),
                sharp + language.getConfigToml().getString("commands.players.revoke"),
                sharp + language.getConfigToml().getString("commands.players.wera"),
                sharp + language.getConfigToml().getString("commands.players.pick"),
                sharp + language.getConfigToml().getString("commands.players.list")
        );
    }

}
