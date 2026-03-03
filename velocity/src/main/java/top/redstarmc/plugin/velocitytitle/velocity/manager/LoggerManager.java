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

import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.core.api.AbstractLoggerManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

/**
 * <b>日志管理器</b><br>
 * 提供了日志相关的操作代码，以免重复编写发送日志的操作。
 */
public class LoggerManager extends AbstractLoggerManager {

    private final CommandSource console;

    public LoggerManager(String INFO_PREFIX, boolean debugMode, CommandSource console) {
        super(INFO_PREFIX, debugMode);
        this.console = console;
    }

    /**
     * <h2>向控制台打印的方法</h2>
     * @param msg 内容
     */
    @Override
    public void sendMessage(String msg) {
        console.sendMessage(TextSer.parseSectionColorCode(msg));
        //
    }

}
