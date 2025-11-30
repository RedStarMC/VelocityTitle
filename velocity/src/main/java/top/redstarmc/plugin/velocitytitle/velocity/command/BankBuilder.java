package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import top.redstarmc.plugin.velocitytitle.velocity.util.FormatConversion;

import static net.kyori.adventure.text.Component.text;

/**
 * 查看玩家拥有的称号列表
 */
public class BankBuilder implements VelocityTitleCommand{
    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("bank")
                .executes(context -> {

                    execute(context.getSource());

                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.string())
                        .requires(source
                                -> source.hasPermission("velocitytitle.bank.other")
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
                            String player = context.getArgument("player", String.class);

                            execute(context.getSource(), player);

                            return 1;
                        })
                );
    }

    private void execute(CommandSource source){
        String player = FormatConversion.sourceToPlayer(source).getUsername();

        if (player == null) return;

        execute(source, player);
    }

    private void execute(CommandSource source, String player){

    }

}
