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

package top.redstarmc.plugin.velocitytitle.velocity.pojo;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.CommandInfo;

import java.util.function.Supplier;

public enum CommandResp {
    SUCCESS(null),
    ERROR(CommandInfo :: error),
    TitleExists(CommandInfo :: existedTitle),
    TitleNotFound(CommandInfo :: cannotFindTitle),
    PlayerOwned(CommandInfo :: ownedTitle),
    PlayerNotOwned(CommandInfo :: notOwnedTitle),
    PlayerNotFound(CommandInfo :: cannotFindPlayer);


    private final Supplier<Component> text;

    CommandResp(Supplier<Component> text) {
        this.text = text;
        //
    }

    public @NotNull Component get() {
        if ( text == null ) {
            return Component.empty();
        }
        return text.get();
    }
}
