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

package top.redstarmc.plugin.velocitytitle.spigot;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.impl.PlayerTitleCache;
import top.redstarmc.plugin.velocitytitle.spigot.manager.CacheManager;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;

public class HookPlaceholderAPI extends PlaceholderExpansion {

    CacheManager cacheManager = VelocityTitleSpigot.getInstance().getCacheManager();

    LoggerManager log = VelocityTitleSpigot.getInstance().getLoggerManager();

    /**
     * 此扩展的占位符标识符。可能不包含 {@literal %},
     * {@literal {}} or _
     *
     * @return 与此扩展关联的占位符标识符
     */
    @Override
    public @NotNull String getIdentifier() {
        return "velocitytitle";
        //
    }

    /**
     * The author of this expansion
     *
     * @return name of the author for this expansion
     */
    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", VelocityTitleSpigot.getInstance().getDescription().getAuthors());
        //
    }

    /**
     * The version of this expansion
     *
     * @return current version of this expansion
     */
    @Override
    public @NotNull String getVersion() {
        return VelocityTitleSpigot.getInstance().getDescription().getVersion();
        //
    }

    @Override
    public boolean persist() {
        return true;
        //
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null || !player.isOnline()) {
            return "UNKNOWN";
        }
        String[] par = params.split("_");
        switch (par[0].toLowerCase()) {
            case "prefix":
                return prefix(player);
            case "suffix":
                return suffix(player);
            case "version":
                return getVersion();
            default:
                log.warn("请求错误！");
                return "";
        }
    }

    public String prefix(@NotNull Player player){
        PlayerTitleCache playerTitleCache = cacheManager.CacheGet(player.getUniqueId().toString());
        if (playerTitleCache.prefix() != null){
            return playerTitleCache.prefix().display();
        }else {
            return "";
        }
    }

    public String suffix(@NotNull Player player){

        PlayerTitleCache playerTitleCache = cacheManager.CacheGet(player.getUniqueId().toString());
        if (playerTitleCache.suffix() != null){
            return playerTitleCache.suffix().display();
        }else {
            return "";
        }
    }

}
