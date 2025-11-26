package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

@Deprecated
public class DeleteBuilder extends CommandBuilder{

    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("delete")
                .requires(source -> source.hasPermission("velocitytitle.delete"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSer.legToCom(
                            language.getConfigToml().getString("commands.parameter-less")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("prefix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSer.legToCom(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);

                                    DataBaseOperate.deleteTitle(context.getSource(), name);

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("suffix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSer.legToCom(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);

                                    DataBaseOperate.deleteTitle(context.getSource(), name);

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                );
    }
}
