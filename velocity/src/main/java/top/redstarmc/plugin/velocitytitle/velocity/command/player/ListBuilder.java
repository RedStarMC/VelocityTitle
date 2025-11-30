package top.redstarmc.plugin.velocitytitle.velocity.command.player;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;

public class ListBuilder implements VelocityTitleCommand {
    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return null;
    }
}
