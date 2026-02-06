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

package top.redstarmc.plugin.velocitytitle.spigot.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.impl.PlayerTitleCache;
import top.redstarmc.plugin.velocitytitle.spigot.VelocityTitleSpigot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    private static final Map<String, PlayerTitleCache> CACHE = new ConcurrentHashMap<>();

    private final LoggerManager logger;

    private final VelocityTitleSpigot plugin;

    public CacheManager(@NotNull LoggerManager logger, @NotNull VelocityTitleSpigot plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    public void CachePut(@NotNull String uuid, @NotNull PlayerTitleCache playerTitle){
        CACHE.put(uuid, playerTitle);
    }

    public PlayerTitleCache CacheGet(@NotNull String uuid){
        //TODO 每隔一段时间发送一次
        PlayerTitleCache playerTitle = CACHE.get(uuid);

        if (playerTitle == null){
            // 发送获取称号请求
            Player player = VelocityTitleSpigot.getInstance().getServer().getPlayer(UUID.fromString(uuid));
            if (player != null && player.isOnline()) {
                plugin.getPluginMessage().sendMessage(player/*其实是PluginMessageRecipient*/, "GetTitle", uuid, "prefix")
                        .thenRunAsync(() -> logger.debug("插件消息：已请求玩家称号前缀数据。UUID: "+uuid));
                plugin.getPluginMessage().sendMessage(player/*其实是PluginMessageRecipient*/, "GetTitle", uuid, "suffix")
                        .thenRunAsync(() -> logger.debug("插件消息：已请求玩家称号后缀数据。UUID: "+uuid));
            }else {
                logger.warn("被请求获取称号的玩家不在线");
            }
            return null;
        }else{
            return playerTitle;
        }
    }

    public void CacheRemove(@NotNull String uuid){
        CACHE.remove(uuid);
        //
    }

    public void CacheRemoveAll(){
        CACHE.clear();
        //
    }


}
