package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;

public class HelpBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("help");
    }
}
