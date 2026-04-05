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

package top.redstarmc.plugin.velocitytitle.core.util;

import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.api.InterfaceCacheManager;
import top.redstarmc.plugin.velocitytitle.core.impl.PlayerTitleCache;
import top.redstarmc.plugin.velocitytitle.core.impl.TitleCache;

public class UpdateTitle {

    public static void updateTitle(@NotNull InterfaceCacheManager cache,
                                   String uuid, String title_name, String title_type, String title_display) {
        PlayerTitleCache playerTitleCache = cache.CacheGet(uuid);

        if ( playerTitleCache == null ) {
            if ( title_type.equals("prefix") ) {
                cache.CachePut(uuid, new PlayerTitleCache(new TitleCache(title_name, title_display), null));

            } else {
                cache.CachePut(uuid, new PlayerTitleCache(null, new TitleCache(title_name, title_display)));

            }
        } else {
            if ( title_type.equals("prefix") ) {
                TitleCache suffix = playerTitleCache.suffix();
                cache.CachePut(uuid, new PlayerTitleCache(new TitleCache(title_name, title_display), suffix));

            } else {
                TitleCache prefix = playerTitleCache.prefix();
                cache.CachePut(uuid, new PlayerTitleCache(prefix, new TitleCache(title_name, title_display)));

            }
        }

    }

}
