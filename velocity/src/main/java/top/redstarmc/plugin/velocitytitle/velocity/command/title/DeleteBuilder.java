package top.redstarmc.plugin.velocitytitle.velocity.command.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;

import static net.kyori.adventure.text.Component.text;

public class DeleteBuilder implements VelocityTitleCommand {
    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("delete")
                .requires(source
                        -> source.hasPermission("velocitytitle.title*")
                        || source.hasPermission("velocitytitle.title.delete")
                        || source.hasPermission("velocitytitle.admin"))
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.word())
                        .executes(context -> {

                            execute(context.getSource(), context.getArgument("name", String.class));

                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    void execute(CommandSource source, String name){
        DataBaseOperate.deleteTitle(source, name);
    }

}
