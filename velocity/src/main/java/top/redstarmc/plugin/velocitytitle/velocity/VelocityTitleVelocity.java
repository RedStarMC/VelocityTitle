package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.velocity.command.CommandBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.TomlManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.LoggerManager;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "velocity_title",
        name = "VelocityTitle"
)
public class VelocityTitleVelocity {

    private LoggerManager logger;

    private TomlManager configManager;

    private final File dataFolder;

    private final ProxyServer server;

    private static VelocityTitleVelocity instance;

    @Inject
    public VelocityTitleVelocity(@DataDirectory Path dataDirectory, ProxyServer server) {
        this.dataFolder = dataDirectory.toFile();
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        System.out.println("[VelocityTitle]  Configurations Loading...");
        configManager = new TomlManager();
        configManager.init();

        logger = new LoggerManager(configManager.getConfigToml().getString("plugin-prefix"),true);

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



    public LoggerManager getLogger() {
        return logger;
    }

    public TomlManager getConfigManager() {
        return configManager;
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
