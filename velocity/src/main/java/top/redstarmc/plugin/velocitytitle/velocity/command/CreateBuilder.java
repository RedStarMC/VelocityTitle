package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;

public class CreateBuilder extends CommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSource> build(ConfigManager language) {
        return LiteralArgumentBuilder.<CommandSource>literal("create")
                .executes(context -> {
                    // ÃüÁî°ïÖú
                    return Command.SINGLE_SUCCESS;
                })
                ;
    }
}
