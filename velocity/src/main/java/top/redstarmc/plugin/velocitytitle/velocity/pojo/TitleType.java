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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TitleType {
    PREFIX("prefix"), SUFFIX("suffix"), ALL("null");

    private final String columnName;

    TitleType(String columnName) {
        this.columnName = columnName;
        //
    }

    public static @Nullable TitleType getType(@NotNull String type) {
        return switch ( type ) {
            case "prefix" -> TitleType.PREFIX;
            case "suffix" -> TitleType.SUFFIX;
            case "all" -> TitleType.ALL;
            default -> null;
        };
    }

    public String getColumnName() {
        return columnName;
        //
    }
}
