package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

/**
 * <h1>创建称号命令</h1>
 */
public class CreateBuilder extends CommandBuilder{


    public RequiredArgumentBuilder<CommandSource, String> preBuild(boolean isPrefix){
        return BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                .executes(context -> {
                    context.getSource().sendMessage(TextSer.legToCom(
                            language.getConfigToml().getString("commands.parameter-less")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("display", StringArgumentType.string())
                        .executes(context -> {
                            context.getSource().sendMessage(TextSer.legToCom(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("description", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);
                                    String display = context.getArgument("display", String.class);
                                    String description = context.getArgument("description", String.class);

                                    DataBaseOperate.insertTitle(sqlManager, name, display, description, isPrefix);

                                    context.getSource().sendMessage(TextSer.legToCom(
                                            language.getConfigToml().getString("commands.create-success")
                                    ));

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                );
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("create")
                .requires(source -> source.hasPermission("velocitytitle.create"))
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(TextSer.legToCom(
                            language.getConfigToml().getString("commands.helps.create-prefix")
                    ));
                    source.sendMessage(TextSer.legToCom(
                            language.getConfigToml().getString("commands.helps.create-suffix")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("prefix")
                        .requires(source -> source.hasPermission("velocitytitle.create"))
                        .executes(context -> {
                            context.getSource().sendMessage(TextSer.legToCom(
                                    language.getConfigToml().getString("commands.helps.create-prefix")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(preBuild(true))
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("suffix")
                        .requires(source -> source.hasPermission("velocitytitle.create"))
                        .executes(context -> {
                            context.getSource().sendMessage(TextSer.legToCom(
                                    language.getConfigToml().getString("commands.helps.create-suffix")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(preBuild(false))
                );
    }

}
