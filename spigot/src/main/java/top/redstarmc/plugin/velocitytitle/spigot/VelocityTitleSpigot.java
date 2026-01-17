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
import top.redstarmc.plugin.velocitytitle.spigot.manager.CacheManager;
import top.redstarmc.plugin.velocitytitle.spigot.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.spigot.manager.LoggerManager;


public class VelocityTitleSpigot extends JavaPlugin implements Listener {

    private static VelocityTitleSpigot instance;

    private LoggerManager logger;

    private PluginMessageBukkit pluginMessage;

    private ConfigManager config;

    private ConfigManager language;

    private CacheManager cacheManager;

    @Override
    public void onEnable() {
        System.out.println("[VelocityTitle] Loading...");
        instance = this;

        System.out.println("[VelocityTitle] Configurations Loading...");
        config = new ConfigManager(getDataFolder(), "config-spigot.toml");
        language = new ConfigManager(getDataFolder(), "language-spigot.toml");
        config.init();language.init();

        logger = new LoggerManager(config.getConfigToml().getString("plugin-prefix"), true);

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
            commands.registrar().register(new CommandBuilder().init().build());
        });

        logger.info(language.getConfigToml().getString("logs.listener-loading"));
        getServer().getPluginManager().registerEvents(this, this);

        logger.info("加载缓存");
        cacheManager = new CacheManager( logger, this);

        logger.info(language.getConfigToml().getString("logs.channel-loading"));
        pluginMessage = new PluginMessageBukkit(this, logger, cacheManager);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HookPlaceholderAPI().register();
        }else {
            logger.error("无法加载PAPI！");
        }

        logger.info(language.getConfigToml().getString("logs.end"));
        logger.warn("当前运行的插件为后端插件，需要在 Velocity 运行 Velocity版插件，否则本插件无法正常运行！");
    }


    @Override
    public void onDisable() {
        cacheManager.asyncCacheRemoveAll();
        //
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        cacheManager.asyncCacheRemove(uuid);
    }


    public static VelocityTitleSpigot getInstance() {
        return instance;
    }

    public LoggerManager getLoggerManager() {
        return logger;
    }

    public ConfigManager getNewConfig() {
        return config;
    }

    public ConfigManager getLanguage() {
        return language;
    }

    public PluginMessageBukkit getPluginMessage() {
        return pluginMessage;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
