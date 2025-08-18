package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSerializers;

public class WearBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("wear")
                .requires(source -> source.hasPermission("velocitytitle.wear"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.helps.create-prefix")
                    ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("suffix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.helps.create-prefix")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.greedyString())
                                .executes(context -> {

                                    String name = context.getArgument("name", String.class);

                                    return Command.SINGLE_SUCCESS;
                                }))
                );
    }
}
