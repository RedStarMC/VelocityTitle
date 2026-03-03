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

public class CommandInfo {

    private static ConfigManager language;

    private static final String sharp = "&9>&r ";

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
                sharp + language.getConfigToml().getString("commands.titlelist.create"),
                sharp + language.getConfigToml().getString("commands.titlelist.edit"),
                sharp + language.getConfigToml().getString("commands.titlelist.delete"),
                sharp + language.getConfigToml().getString("commands.titlelist.list"),
                sharp + language.getConfigToml().getString("commands.titlelist.meta")
        );
    }

    public static @NotNull List<Component> player() {
        return TextSer.toComponentList(
                prefix() + language.getConfigToml().getString("commands.player"),
                sharp + language.getConfigToml().getString("commands.playerlist.divide"),
                sharp + language.getConfigToml().getString("commands.playerlist.revoke"),
                sharp + language.getConfigToml().getString("commands.playerlist.wear"),
                sharp + language.getConfigToml().getString("commands.playerlist.pick"),
                sharp + language.getConfigToml().getString("commands.playerlist.list")
        );
    }

    public static @NotNull Component argumentMiss() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.argument-miss"));
    }

    public static class Title {

        public static @NotNull List<Component> create() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.titlelist.create"),
                    language.getConfigToml().getString("commands.titlelist.create-p"));
        }

        public static @NotNull List<Component> edit() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.titlelist.edit"),
                    language.getConfigToml().getString("commands.titlelist.edit-p"));
        }

        public static @NotNull List<Component> delete() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.titlelist.delete"),
                    language.getConfigToml().getString("commands.titlelist.delete-p"));
        }

        public static @NotNull List<Component> meta() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.titlelist.meta"),
                    language.getConfigToml().getString("commands.titlelist.meta-p"));
        }

    }

    /*
    错误区
     */

    public static class Player {

        public static @NotNull List<Component> divide() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.playerlist.divide"),
                    language.getConfigToml().getString("commands.playerlist.divide-p"));
        }

        public static @NotNull List<Component> revoke() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.playerlist.revoke"),
                    language.getConfigToml().getString("commands.playerlist.revoke-p"));
        }

        public static @NotNull List<Component> pick() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.playerlist.pick"),
                    language.getConfigToml().getString("commands.playerlist.pick-p"));
        }

        public static @NotNull List<Component> wear() {
            return TextSer.toComponentList(prefix() + language.getConfigToml().getString("commands.playerlist.wear"),
                    language.getConfigToml().getString("commands.playerlist.wear-p"));
        }

    }

    public static @NotNull Component error() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.error"));
        //
    }

    public static @NotNull Component cannotFindTitle() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.cannot-find-title"));
        //
    }

    public static @NotNull Component cannotFindPlayer() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.cannot-find-player"));
        //
    }

    public static @NotNull Component existedTitle() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.existed-title"));
        //
    }

    public static @NotNull Component ownedTitle() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.owned-title"));
        //
    }

    public static @NotNull Component notOwnedTitle() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.not-owned-title"));
        //
    }


    /*
    成功区
     */

    public static @NotNull Component titleCreateSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.title-create-success"));
        //
    }

    public static @NotNull Component titleDeleteSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.title-delete-success"));
        //
    }

    public static @NotNull Component titleUpdateSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.title-update-success"));
        //
    }

    public static @NotNull Component divideSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.divide-success"));
        //
    }

    public static @NotNull Component revokeSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.revoke-success"));
        //
    }

    public static @NotNull Component wearSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.wear-success"));
        //
    }

    public static @NotNull Component pickSuccess() {
        return TextSer.legToCom(prefix() + language.getConfigToml().getString("commands.response.pick-success"));
        //
    }

}
