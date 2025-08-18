package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.database.operate.PlayerTitlesOperate;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.EasySQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSerializers;

import static net.kyori.adventure.text.Component.text;

public class RevokeBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("revoke")
                .requires(source -> source.hasPermission("velocitytitle.revoke"))
                .executes(context -> {
                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                            language.getConfigToml().getString("commands.helps.create-prefix")
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
                        .then(BrigadierCommand.requiredArgumentBuilder("player_name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    proxyServer.getAllPlayers().forEach(player -> builder.suggest(
                                            player.getUsername(),
                                            VelocityBrigadierMessage.tooltip(text(player.getUsername()))
                                    ));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                            language.getConfigToml().getString("commands.parameter-less")
                                    ));
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.greedyString())
                                        .executes(context -> {

                                            String player_name = context.getArgument("player_name", String.class);
                                            String name = context.getArgument("name", String.class);
                                            String uuid = VelocityTitleVelocity.getInstance().getServer().getPlayer(player_name).orElse(null).getUniqueId().toString();

                                            PlayerTitlesOperate.deletePrefix(EasySQLManager.getSqlManager(), uuid , name);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("suffix")
                        .executes(context -> {
                            context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                    language.getConfigToml().getString("commands.parameter-less")
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("player_name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    proxyServer.getAllPlayers().forEach(player -> builder.suggest(
                                            player.getUsername(),
                                            VelocityBrigadierMessage.tooltip(text(player.getUsername()))
                                    ));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    context.getSource().sendMessage(TextSerializers.legacyToComponent(
                                            language.getConfigToml().getString("commands.parameter-less")
                                    ));
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.greedyString())
                                        .executes(context -> {

                                            String player_name = context.getArgument("player_name", String.class);
                                            String name = context.getArgument("name", String.class);
                                            String uuid = VelocityTitleVelocity.getInstance().getServer().getPlayer(player_name).orElse(null).getUniqueId().toString();

                                            PlayerTitlesOperate.deleteSuffix(EasySQLManager.getSqlManager(), uuid , name);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                );
    }
}
