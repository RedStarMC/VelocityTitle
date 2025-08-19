package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import top.redstarmc.plugin.velocitytitle.velocity.database.operate.PlayerWearOperate;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.EasySQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSerializers;

public class WearBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("wear")
                .requires(source -> source.hasPermission("velocitytitle.wear"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.parameter-less")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("suffix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.greedyString())
                                .executes(context -> {

                                    String name = context.getArgument("name", String.class);
                                    String uuid;
                                    if(context.getSource() instanceof Player player){
                                        uuid = player.getUniqueId().toString();
                                    }else {
                                        context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                                language.getConfigToml().getString("commands.console")
                                        ));
                                        return -1;
                                    }

                                    PlayerWearOperate.updateSuffix(EasySQLManager.getSqlManager(), uuid, name);

                                    return Command.SINGLE_SUCCESS;
                                }))
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("prefix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.greedyString())
                                .executes(context -> {

                                    String name = context.getArgument("name", String.class);
                                    String uuid;
                                    if(context.getSource() instanceof Player player){
                                        uuid = player.getUniqueId().toString();
                                    }else {
                                        context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                                language.getConfigToml().getString("commands.console")
                                        ));
                                        return -1;
                                    }

                                    PlayerWearOperate.updatePrefix(EasySQLManager.getSqlManager(), uuid, name);

                                    return Command.SINGLE_SUCCESS;
                                }))
                );
    }
}
