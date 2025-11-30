package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.CommandHelp;

public class HelpBuilder implements VelocityTitleCommand {

    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("help")
                .executes(context -> {
                    context.getSource().sendMessage(CommandHelp.help());
                    return 1;
                });
    }

}
