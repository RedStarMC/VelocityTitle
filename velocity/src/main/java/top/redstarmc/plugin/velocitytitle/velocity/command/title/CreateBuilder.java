package top.redstarmc.plugin.velocitytitle.velocity.command.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate;

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
                .requires(source
                        -> source.hasPermission("velocitytitle.title*")
                        || source.hasPermission("velocitytitle.title.create")
                        || source.hasPermission("velocitytitle.admin"))
                .executes(context -> {
                    context.getSource().sendMessage(text("帮助"));
                    return 1;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("type", StringArgumentType.word())
                        .executes(context -> 1)
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.word())
                                .executes(context -> 1)
                                .then(BrigadierCommand.requiredArgumentBuilder("display", StringArgumentType.string())
                                        .executes(context -> {
                                            String type = context.getArgument("type", String.class);
                                            String name = context.getArgument("name", String.class);
                                            String display = context.getArgument("display", String.class);
                                            String description = "无";

                                            execute(context.getSource(), type, name, display, description);

                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .then(BrigadierCommand.requiredArgumentBuilder("description", StringArgumentType.greedyString())
                                                .executes(context -> {
                                                    String type = context.getArgument("type", String.class);
                                                    String name = context.getArgument("name", String.class);
                                                    String display = context.getArgument("display", String.class);
                                                    String description = context.getArgument("description", String.class);

                                                    execute(context.getSource(), type, name, display, description);

                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                )
                        )
                );
    }

    private void execute(CommandSource source, @NotNull String type, String name, String display, String description){
        boolean isPrefix;
        if  (type.equals("prefix") || type.equals("pre") || type.equals("p")){
            isPrefix = true;
        } else if (type.equals("suffix") || type.equals("suf") || type.equals("s")) {
            isPrefix = false;
        }else {
            source.sendMessage(text("错误信息")); //TODO
            return;
        }

        DataBaseOperate.insertTitle(source, name, display, description, isPrefix);

    }

}
