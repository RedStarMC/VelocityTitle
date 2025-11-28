package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;

import static net.kyori.adventure.text.Component.text;

public class WearBuilder implements VelocityTitleCommand{

    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("wear")
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.word())
                        .executes(context -> { // 给自己改
                            String name = context.getArgument("name", String.class);

                            execute(context.getSource(), name);

                            return 1;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.string())
                                .requires(source
                                    -> source.hasPermission("velocitytitle.wear.other")
                                    || source.hasPermission("velocitytitle.admin")
                                )
                                .suggests((context, builder) -> { // 提供所有的玩家名字
                                    proxyServer.getAllPlayers().forEach(player -> builder.suggest(
                                            player.getUsername(),
                                            VelocityBrigadierMessage.tooltip(text(player.getUsername()))
                                    ));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);
                                    String player = context.getArgument("player", String.class);

                                    execute(context.getSource(), name, player);

                                    return 1;
                                })
                        )
                );
    }

    private void execute(CommandSource source, String name, String player){

    }

    private void execute(CommandSource source, String name){

    }
}
