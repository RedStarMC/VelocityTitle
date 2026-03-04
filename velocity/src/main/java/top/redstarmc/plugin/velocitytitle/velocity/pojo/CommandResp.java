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

import java.util.function.Function;

/**
 * 第一个参数永远是 称号
 * 第二个参数永远是 玩家名
 */
public enum CommandResp {
    SUCCESS(args -> Component.empty()), // 成功无消息
    ERROR(args -> CommandInfo.error()),
    TitleExists(args -> CommandInfo.existedTitle((String) args[0])),
    TitleNotFound(args -> CommandInfo.cannotFindTitle((String) args[0])),
    PlayerOwned(args -> CommandInfo.ownedTitle((String) args[1], (String) args[0])),
    PlayerNotOwned(args -> CommandInfo.notOwnedTitle((String) args[1], (String) args[0])),
    PlayerNotFound(args -> CommandInfo.cannotFindPlayer((String) args[1]));

    private final Function<Object[], Component> messageFunc;

    CommandResp(Function<Object[], Component> messageFunc) {
        this.messageFunc = messageFunc;
        //
    }

    public @NotNull Component get(Object... args) {
        return messageFunc.apply(args);
    }
}
