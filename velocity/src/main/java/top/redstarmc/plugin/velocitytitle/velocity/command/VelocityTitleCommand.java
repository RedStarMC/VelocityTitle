package top.redstarmc.plugin.velocitytitle.velocity.command;

import cc.carm.lib.easysql.api.SQLManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.core.api.TitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.command.player.DivideBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.player.RevokeBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.CreateBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.DeleteBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.EditBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.ListBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;

import static net.kyori.adventure.text.Component.text;

public interface VelocityTitleCommand extends TitleCommand {

    /**
     * 语言文件
     */
    static ConfigManager language = VelocityTitleVelocity.getInstance().getLanguage();

    /**
     * 服务器实例
     */
    static ProxyServer proxyServer = VelocityTitleVelocity.getInstance().getServer();

    /**
     * 数据库实例
     */
    static SQLManager sqlManager = VelocityTitleVelocity.getInstance().getDBManager().getSqlManager();

    /**
     * 子命令树
     * @return 直接通过添加到 then() 添加的命令树
     */
    LiteralArgumentBuilder<CommandSource> build();

    /**
     * 初始化命令方法
     * @return 整个命令树
     */
    static LiteralCommandNode<CommandSource> init(){
        return LiteralArgumentBuilder.<CommandSource>literal("VelocityTitle")
                .executes(context -> {
                    context.getSource().sendMessage(text(language.getConfigToml().getString("commands.root"))
                            .append(text(language.getConfigToml().getString("commands.helps.open"))));
                    return Command.SINGLE_SUCCESS;
                })
                .then(new HelpBuilder().build())
                .then(new ReloadBuilder().build())
                .then(title())
                .then(player())
                .then(database())
                .then(new WearBuilder().build())
                .then(new PickBuilder().build())
                .then(new BankBuilder().build())
                .build();
    }

    private static LiteralArgumentBuilder<CommandSource> title() {
        return LiteralArgumentBuilder.<CommandSource>literal("title")
                .requires(source
                        -> source.hasPermission("velocitytitle.title*")
                        || source.hasPermission("velocitytitle.title")
                        || source.hasPermission("velocitytitle.admin")
                )
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(new CreateBuilder().build())
                .then(new EditBuilder().build())
                .then(new DeleteBuilder().build())
                .then(new ListBuilder().build());
    }

    private static LiteralArgumentBuilder<CommandSource> database(){
        return null;
    }

    private static LiteralArgumentBuilder<CommandSource> player(){
        return LiteralArgumentBuilder.<CommandSource>literal("player")
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(new DivideBuilder().build())
                .then(new RevokeBuilder().build());
    }

}
