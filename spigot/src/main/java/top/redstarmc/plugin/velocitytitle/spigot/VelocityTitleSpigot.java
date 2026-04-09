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

package top.redstarmc.plugin.velocitytitle.spigot;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.redstarmc.plugin.velocitytitle.spigot.manager.SGCacheManager;
import top.redstarmc.plugin.velocitytitle.spigot.manager.SGConfigManager;
import top.redstarmc.plugin.velocitytitle.spigot.manager.SGLoggerManager;


public class VelocityTitleSpigot extends JavaPlugin implements Listener {

    private static VelocityTitleSpigot instance;

    private SGLoggerManager logger;

    private SGPluginMessage pluginMessage;

    private SGConfigManager config;

    private SGConfigManager language;

    private SGCacheManager cacheManager;

    @Override
    public void onEnable() {
        System.out.println("[VelocityTitle] Loading...");
        instance = this;

        System.out.println("[VelocityTitle] Configurations Loading...");
        config = new SGConfigManager(getDataFolder(), "config-spigot.toml");
        language = new SGConfigManager(getDataFolder(), "language-spigot.toml");
        config.init();language.init();

        logger = new SGLoggerManager(config.getConfigToml().getString("plugin-prefix"),
                config.getConfigToml().getBoolean("debug-mode"));

        logger.info("Language: "+language.getConfigToml().getString("name"));

        logger.info(language.getConfigToml().getString("logs.loading"));
        logger.info(language.getConfigToml().getString("logs.author")," pingguomc");
        logger.debug(language.getConfigToml().getString("logs.debug"));
        logger.info(language.getConfigToml().getString("logs.website")," https://github.com/RedStarMC/VelocityTitle");

        //=========
        // TODO 检查服务端是否启用 Velocity 模式
        //=========

        logger.info(language.getConfigToml().getString("logs.command-loading"));
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new SGCommandBuilder().init().build());
        });

        logger.info(language.getConfigToml().getString("logs.listener-loading"));
        getServer().getPluginManager().registerEvents(this, this);

        logger.info("加载称号缓存管理器");
        cacheManager = new SGCacheManager(logger, this);

        logger.info(language.getConfigToml().getString("logs.channel-loading"));
        pluginMessage = new SGPluginMessage(this, logger, cacheManager);

        int a = 1;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HookPlaceholderAPI().register();
            logger.info("PlaceholderAPI 已加载！");
            a++;
        }else {
            logger.debug("PlaceholderAPI 未安装！");
        }

        if ( Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders") ) {
            new HookMiniPlaceholderAPI().init();
            logger.info("MiniPlaceholders 已加载！");
            a++;
        } else {
            logger.debug("MiniPlaceholders 未安装！");
        }
        if ( a < 2 ) logger.warn("没有安装 PlaceholderAPI 或 MiniPlaceholders 占位符插件，你将无法使用本插件！");

        logger.info(language.getConfigToml().getString("logs.end"));
        logger.warn("当前运行的插件为后端插件，需要在 Velocity 运行 Velocity版插件，并在本服务器上安装占位符插件。否则本插件无法正常运行！");
    }

    @Override
    public void onDisable() {
        cacheManager.CacheRemoveAll();
        //
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        cacheManager.CacheRemove(uuid);
    }


    public static VelocityTitleSpigot getInstance() {
        return instance;
    }

    public SGLoggerManager getLoggerManager() {
        return logger;
    }

    public SGConfigManager getNewConfig() {
        return config;
    }

    public SGConfigManager getLanguage() {
        return language;
    }

    public SGPluginMessage getPluginMessage() {
        return pluginMessage;
    }

    public SGCacheManager getCacheManager() {
        return cacheManager;
    }
}
