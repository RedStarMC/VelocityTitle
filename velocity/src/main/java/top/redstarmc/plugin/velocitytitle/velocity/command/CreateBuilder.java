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

/**
 * <h1>创建称号</h1>
 */
public class CreateBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("create")
                .requires(source -> source.hasPermission("velocitytitle.create"))
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.helps.create-prefix")
                    ));
                    source.sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.helps.create-suffix")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(prefix(language))
                .then(suffix(language));
    }

    public LiteralArgumentBuilder<CommandSource> prefix(ConfigManager language){
        return LiteralArgumentBuilder.<CommandSource>literal("prefix")
                .requires(source -> source.hasPermission("velocitytitle.create"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.helps.create-prefix")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("display", StringArgumentType.string())
                                        .executes(context -> {
                                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                                    language.getConfigToml().getString("commands.parameter-less")
                                            ));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .then(BrigadierCommand.requiredArgumentBuilder("description", StringArgumentType.greedyString())
                                                .executes(context -> {
                                                    String name = context.getArgument("name", String.class);
                                                    String display = context.getArgument("display", String.class);
                                                    String description = context.getArgument("description", String.class);

                                                    TitleDictionary.insertTitle(EasySQLManager.getSqlManager(), name, display , description);

                                                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                                            language.getConfigToml().getString("commands.create-success")
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                        )
                );
    }

    public LiteralArgumentBuilder<CommandSource> suffix(ConfigManager language){
        return LiteralArgumentBuilder.<CommandSource>literal("suffix")
                .requires(source -> source.hasPermission("velocitytitle.create"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.helps.create-suffix")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("display", StringArgumentType.string())
                                .executes(context -> {
                                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                            language.getConfigToml().getString("commands.parameter-less")
                                    ));
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(BrigadierCommand.requiredArgumentBuilder("description", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String name = context.getArgument("name", String.class);
                                            String display = context.getArgument("display", String.class);
                                            String description = context.getArgument("description", String.class);

                                            SuffixDictionaryOperate.insertTitle(EasySQLManager.getSqlManager(), name, display , description);

                                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                                    language.getConfigToml().getString("commands.create-success")
                                            ));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                );
    }
}
