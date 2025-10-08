package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.database.operate.TitleDictionary;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.EasySQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSerializers;

public class DeleteBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("delete")
                .requires(source -> source.hasPermission("velocitytitle.delete"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.parameter-less")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("prefix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);

                                    TitleDictionary.deleteTitle(EasySQLManager.getSqlManager(), name);

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("suffix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);

//                                    SuffixDictionaryOperate.deleteTitle(EasySQLManager.getSqlManager(), name);

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                );
    }
}
