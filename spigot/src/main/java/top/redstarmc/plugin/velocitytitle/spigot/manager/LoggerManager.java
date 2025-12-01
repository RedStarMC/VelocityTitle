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

import org.bukkit.Bukkit;
import top.redstarmc.plugin.velocitytitle.core.api.AbstractLoggerManager;

public class LoggerManager extends AbstractLoggerManager {

    public LoggerManager(String INFO_PREFIX, boolean debugMode) {
        super(INFO_PREFIX, debugMode);
    }

    /**
     * <h2>向控制台打印的方法</h2>
     * @param msg 内容
     */
    @Override
    public void sendMessage(String... msg) {
        for (String s : msg) {
            if (s == null) continue;
            Bukkit.getConsoleSender().sendMessage(s);
        }
    }
}
