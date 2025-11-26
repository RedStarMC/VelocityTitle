package top.redstarmc.plugin.velocitytitle.velocity.command.title;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;

import static net.kyori.adventure.text.Component.text;

public class CreateBuilder implements VelocityTitleCommand {

    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("create")
                .requires(source -> {
                    return source.hasPermission("velocitytitle.title*")
                            || source.hasPermission("velocitytitle.title.create")
                            || source.hasPermission("velocitytitle.admin");
                })
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                });
    }

}
