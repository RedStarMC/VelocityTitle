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

package top.redstarmc.plugin.velocitytitle.velocity.command;

import cc.carm.lib.easysql.api.SQLManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.command.player.DivideBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.player.RevokeBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.CreateBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.DeleteBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.EditBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.command.title.ListBuilder;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.CommandHelp;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

import static net.kyori.adventure.text.Component.text;

/**
 * <h1>插件命令器</h1>
 * 作为接口规范其他命令实现
 * 合并、注册命令树
 */
public interface VelocityTitleCommand {

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
                    TextSer.sendComponentList(context.getSource(), CommandHelp.root());
                    return Command.SINGLE_SUCCESS;
                })
                .then(title())
                .then(player())
                .then(new ReloadBuilder().build())
                .then(new WearBuilder().build())
                .then(new PickBuilder().build())
                .then(new BankBuilder().build())
                //.then(new MetaBuilder().build())
                .then(LiteralArgumentBuilder.<CommandSource>literal("help")
                        .executes(context -> {
                            TextSer.sendComponentList(context.getSource(), CommandHelp.help());
                            return 1;
                        })
                )
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
                    TextSer.sendComponentList(context.getSource(), CommandHelp.title());
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
