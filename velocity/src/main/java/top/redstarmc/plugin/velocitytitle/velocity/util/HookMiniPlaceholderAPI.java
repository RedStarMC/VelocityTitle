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

package top.redstarmc.plugin.velocitytitle.velocity.util;

import com.velocitypowered.api.proxy.Player;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import top.redstarmc.plugin.velocitytitle.core.impl.PlayerTitleCache;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.VCCacheManager;

public class HookMiniPlaceholderAPI {

    final VCCacheManager VCCacheManager = VelocityTitleVelocity.getInstance().getCacheManager();

    public void init() {
        Expansion.Builder builder = Expansion.builder("velocitytitle");

        builder.audiencePlaceholder(Player.class, "prefix", (Player player, ArgumentQueue queue, Context ctx) -> {
            PlayerTitleCache playerTitleCache = VCCacheManager.CacheGet(player.getUniqueId().toString());
            if ( playerTitleCache != null && playerTitleCache.prefix() != null ) {
                return Tag.selfClosingInserting(LegacyComponentSerializer.legacyAmpersand().deserialize(playerTitleCache.prefix().display()));
            } else {
                return Tag.selfClosingInserting(Component.empty());
            }
        });

        builder.audiencePlaceholder(Player.class, "suffix", (Player player, ArgumentQueue queue, Context ctx) -> {
            PlayerTitleCache playerTitleCache = VCCacheManager.CacheGet(player.getUniqueId().toString());
            if ( playerTitleCache != null && playerTitleCache.suffix() != null ) {
                return Tag.selfClosingInserting(LegacyComponentSerializer.legacyAmpersand().deserialize(playerTitleCache.suffix().display()));
            } else {
                return Tag.selfClosingInserting(Component.empty());
            }
        });

        builder.build().register();

    }

}
