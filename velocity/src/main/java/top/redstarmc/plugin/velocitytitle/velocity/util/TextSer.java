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

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>文本序列号器</h1>
 */
public final class TextSer {

    /**
     * 将 legacy 格式的字符串转为现代的 {@link Component} 消息组件
     * @param text legacy 格式的字符串
     * @return {@link Component} 组件
     */
    public static @NotNull Component legToCom(String text){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    /**
     * 将许多 legacy 格式的字符串 转为现代的 {@link Component} 消息组件列表
     * @param messages legacy 格式的字符串
     * @return {@link List}<{@link Component}>
     */
    public static @NotNull List<Component> toComponentList(String @NotNull ... messages) {
        List<Component> message_list = new ArrayList<Component>();
        for (String message : messages) {
            message_list.add(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
        return message_list;
    }

    public static void sendComponentList(@NotNull CommandSource source, @NotNull List<Component> components){
        for (Component component : components){
            source.sendMessage(component);
        }
    }

}
