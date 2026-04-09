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

package top.redstarmc.plugin.velocitytitle.velocity.manager;

import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.api.InterfaceCacheManager;
import top.redstarmc.plugin.velocitytitle.core.impl.PlayerTitleCache;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;
import top.redstarmc.plugin.velocitytitle.velocity.pojo.TitleType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h2>缓存管理器</h2>
 */
public class VCCacheManager implements InterfaceCacheManager {

    private static final Map<String, PlayerTitleCache> CACHE = new ConcurrentHashMap<>();

    private final VCLoggerManager logger;

    /** 插件实例 */
    private final VelocityTitleVelocity plugin;

    public VCCacheManager(@NotNull VCLoggerManager logger, @NotNull VelocityTitleVelocity plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    /**
     * 向缓存中保存指定的称号
     *
     * @param uuid        玩家 UUID
     * @param playerTitle 称号
     */
    @Override
    public void CachePut(@NotNull String uuid, @NotNull PlayerTitleCache playerTitle) {
        CACHE.put(uuid, playerTitle);
    }

    @Override
    public PlayerTitleCache CacheGet(@NotNull String uuid) {
        PlayerTitleCache playerTitle = CACHE.get(uuid);

        if ( playerTitle == null ) {

            DataBaseOperate.playerWoreTitle(uuid, TitleType.ALL)
                    .thenAcceptAsync(result -> {
                        CachePut(uuid, result);
                    });

            return null;
        } else {
            return playerTitle;
        }
    }

    @Override
    public void CacheRemove(@NotNull String uuid) {
        CACHE.remove(uuid);
        //
    }

    @Override
    public void CacheRemoveAll() {
        CACHE.clear();
        //
    }


}
