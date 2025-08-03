package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.velocity.command.CommandBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.Config;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.Language;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "velocity_title",
        name = "VelocityTitle"
)
public class VelocityTitleVelocity {

    private LoggerManager logger;

    private final File dataFolder;

    private Config config;

    private Language language;

    private final ProxyServer server;

    private static VelocityTitleVelocity instance;

    @Inject
    public VelocityTitleVelocity(@DataDirectory Path dataDirectory, ProxyServer server) {
        this.dataFolder = dataDirectory.toFile();
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        System.out.println("[VelocityTitle] Loading...");
        instance = this;

        System.out.println("[VelocityTitle] Configurations Loading...");
        loadConfiguration();

        logger = new LoggerManager(config.getConfigToml().getString("plugin-prefix"),
                config.getConfigToml().getBoolean("debug-mode"));

        logger.info(config.getConfigToml().getString("loading"));
        logger.info(config.getConfigToml().getString("author")," pingguomc");
        logger.debug(config.getConfigToml().getString("debug"));
        logger.info(config.getConfigToml().getString("website")," https://github.com/RedStarMC/VelocityTitle");

        logger.info(config.getConfigToml().getString("command-loading"));
        registerCommand();

        logger.info(config.getConfigToml().getString("database-loading"));



        logger.info("测试");
        logger.warn("警告");
        logger.error("错误");
        logger.debug("debug");
    }



    private void registerCommand(){
        CommandManager commandManager = server.getCommandManager();

        CommandMeta commandMeta = commandManager.metaBuilder("VelocityTitle")
                .plugin(this)
                .aliases("vt")
                .build();

        commandManager.register(commandMeta,new BrigadierCommand(CommandBuilder.init()));
    }


    private void loadConfiguration(){
        config = new Config(this.getDataFolder());
        config.init();

        language = new Language(this.getDataFolder());
        language.init();
    }


    public LoggerManager getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public static VelocityTitleVelocity getInstance() {
        return instance;
    }
}
