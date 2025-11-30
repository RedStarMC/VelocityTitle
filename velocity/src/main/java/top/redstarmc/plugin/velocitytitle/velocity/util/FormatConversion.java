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

package top.redstarmc.plugin.velocitytitle.velocity.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;

import java.util.UUID;

/**
 * <h1>格式转换器</h1>
 */
public final class FormatConversion {

    /**
     * @param uuid 玩家的 {@link UUID}
     * @return {@link Player} 实例
     */
    public static @Nullable Player uuidToPlayer(UUID uuid){
        return VelocityTitleVelocity.getInstance().getServer().getPlayer(uuid).orElse(null);
    }

    /**
     * @param player_name 玩家名称
     * @return {@link Player} 玩家实例
     */
    public static @Nullable Player nameToPlayer(@NotNull String player_name){
        return VelocityTitleVelocity.getInstance().getServer().getPlayer(player_name).orElse(null);
    }

    /**
     * @param player_name 玩家名称
     * @return 玩家的 {@link UUID}
     */
    public static @Nullable UUID nameToUUID(String player_name){
        Player player = VelocityTitleVelocity.getInstance().getServer().getPlayer(player_name).orElse(null);
        if (player != null) return player.getUniqueId();
        else return null;
    }

    public static Player sourceToPlayer(@NotNull CommandSource source){
        if(source instanceof Player player){
            return player;
        }else {
            return null;
        }
    }

}
