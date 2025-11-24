package top.redstarmc.plugin.velocitytitle.velocity.command;

import cc.carm.lib.easysql.api.SQLManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;

import static net.kyori.adventure.text.Component.text;

/**
 * <h1>ÃüÁî¹¹½¨Æ÷</h1>
 */
public abstract class CommandBuilder {

    public static final ConfigManager language = VelocityTitleVelocity.getInstance().getLanguage();

    public static final ProxyServer proxyServer = VelocityTitleVelocity.getInstance().getServer();

    protected static SQLManager sqlManager = VelocityTitleVelocity.getInstance().getDBManager().getSqlManager();

    public abstract LiteralArgumentBuilder<CommandSource> build();

    public static LiteralCommandNode<CommandSource> init(){
        return LiteralArgumentBuilder.<CommandSource>literal("VelocityTitle")
                .executes(context -> {
                    context.getSource().sendMessage(text(language.getConfigToml().getString("commands.root"))
                            .append(text(language.getConfigToml().getString("commands.helps.open"))));
                    return Command.SINGLE_SUCCESS;
                })
                .then(new CreateBuilder().build())
                .then(new DeleteBuilder().build())
                .then(new GiveBuilder().build())
                .then(new HelpBuilder().build())
                .then(new ReloadBuilder().build())
                .then(new RevokeBuilder().build())
                .build();
    }

}
