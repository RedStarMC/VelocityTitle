package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;

public abstract class CommandBuilder {

    public static LiteralCommandNode<CommandSource> init(){
        return LiteralArgumentBuilder.<CommandSource>literal("VelocityTitle")
                .requires(source -> source.hasPermission("VelocityTitle.info"))
                .executes(context -> {
                    context.getSource().sendMessage(Component.text("a"));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    public static final ProxyServer proxyServer = VelocityTitleVelocity.getInstance().getServer();

    public abstract LiteralArgumentBuilder<CommandSource> build();
}
