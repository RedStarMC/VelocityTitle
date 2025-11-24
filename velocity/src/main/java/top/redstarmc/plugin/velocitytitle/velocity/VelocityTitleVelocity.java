package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.velocity.command.CommandBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.Config;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.Language;
import top.redstarmc.plugin.velocitytitle.velocity.manager.EasySQLManager;
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

    private EasySQLManager DBManager;

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

        logger.info("Language: "+language.getConfigToml().getString("name"));

        logger.info(language.getConfigToml().getString("logs.loading"));
        logger.info(language.getConfigToml().getString("logs.author")," pingguomc");
        logger.debug(language.getConfigToml().getString("logs.debug"));
        logger.info(language.getConfigToml().getString("logs.website")," https://github.com/RedStarMC/VelocityTitle");

        logger.info(language.getConfigToml().getString("logs.command-loading"));
        registerCommand();

        logger.info(language.getConfigToml().getString("logs.database-loading"));
        DBManager = new EasySQLManager(logger, config, language);
        DBManager.init();

        logger.info(language.getConfigToml().getString("logs.listener-loading"));
        server.getEventManager().register(this, new Listener());

        logger.info(language.getConfigToml().getString("logs.channel-loading"));
        server.getChannelRegistrar().register(PluginMessage.INCOMING, PluginMessage.OUTGOING);

        logger.info(language.getConfigToml().getString("logs.end"));
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event){
        loadConfiguration();
        server.getCommandManager().unregister("VelocityTitle");
        registerCommand();
        DBManager.init();
        logger.info(language.getConfigToml().getString("logs.reload"));
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

    public Language getLanguage() {
        return language;
    }

    public EasySQLManager getDBManager() {
        return DBManager;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public static VelocityTitleVelocity getInstance() {
        return instance;
    }
}
