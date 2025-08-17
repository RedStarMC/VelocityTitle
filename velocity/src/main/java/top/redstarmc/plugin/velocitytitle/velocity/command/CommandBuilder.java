package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;

import static net.kyori.adventure.text.Component.text;

public abstract class CommandBuilder {

    public static LiteralCommandNode<CommandSource> init(ConfigManager language){
        return LiteralArgumentBuilder.<CommandSource>literal("VelocityTitle")
                .executes(context -> {
                    context.getSource().sendMessage(text(language.getConfigToml().getString("commands.root"))
                            .append(text(language.getConfigToml().getString("commands.helps.open"))));
                    return Command.SINGLE_SUCCESS;
                })
                .then(CreateBuilder.init(language))
                .build();

    }

    public static final ProxyServer proxyServer = VelocityTitleVelocity.getInstance().getServer();

    public abstract LiteralArgumentBuilder<CommandSource> build(ConfigManager language);
}
