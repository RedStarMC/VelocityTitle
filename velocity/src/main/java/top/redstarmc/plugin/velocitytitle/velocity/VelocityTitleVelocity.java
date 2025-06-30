package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import top.redstarmc.plugin.velocitytitle.velocity.command.CommandBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.util.LoggerManager;

@Plugin(
        id = "velocity_title",
        name = "VelocityTitle"
)
public class VelocityTitleVelocity {

    private LoggerManager logger;

    @Inject
    private ProxyServer server;

    private static VelocityTitleVelocity instance;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        logger = new LoggerManager("测试浅醉",true);
        logger.info("测试");
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

    public ProxyServer getServer() {
        return server;
    }

    public static VelocityTitleVelocity getInstance() {
        return instance;
    }
}
