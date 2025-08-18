package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSerializers;

public class ReloadBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("reload")
                .requires(source -> source.hasPermission("velocitytitle.reload"))
                .executes(context -> {

                    VelocityTitleVelocity.getInstance().onProxyReload(new ProxyReloadEvent());

                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("logs.reload")
                    ));

                    return Command.SINGLE_SUCCESS;
                });
    }
}
