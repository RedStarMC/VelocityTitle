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

/**
 * 称号实例
 * @param id 称号ID，唯一标识
 * @param name 称号名称，唯一标识
 * @param display 展示内容
 * @param description 描述
 * @param isPrefix 是否是前缀
 */
public record Title(int id, @NotNull String name, @NotNull String display, @NotNull String description, boolean isPrefix) { }
