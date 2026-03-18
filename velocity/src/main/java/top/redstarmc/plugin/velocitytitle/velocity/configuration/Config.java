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

package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import top.redstarmc.plugin.velocitytitle.velocity.manager.VelocityConfigManager;

import java.io.File;

public class Config extends VelocityConfigManager {

    static final String fileName = "config-velocity.toml";

    public String pluginPrefix;

    public boolean debugMode;

    public String language;

    public boolean confirmMode;

    public Config(File dataFolder) {
        super(dataFolder, fileName);
        //
    }

    @Override
    public void init() {
        super.init();
        this.pluginPrefix = getConfigToml().getString("plugin-prefix", "[VelocityTitle]");
        this.debugMode = getConfigToml().getBoolean("debugMode", false);
        this.language = getConfigToml().getString("language", "zh_CN");
        this.confirmMode = getConfigToml().getBoolean("confirm.enable", true);
    }

}
