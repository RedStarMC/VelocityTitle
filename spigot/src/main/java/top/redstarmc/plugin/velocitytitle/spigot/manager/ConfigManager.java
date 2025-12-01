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

import top.redstarmc.plugin.velocitytitle.core.api.AbstractTomlManager;

import java.io.File;

public class ConfigManager extends AbstractTomlManager {

    /**
     * 构造器
     *
     * @param dataFolder 插件的工作文件夹
     * @param fileName   要操作的配置文件名称
     */
    public ConfigManager(File dataFolder, String fileName) {
        super(dataFolder, fileName);
    }


}
