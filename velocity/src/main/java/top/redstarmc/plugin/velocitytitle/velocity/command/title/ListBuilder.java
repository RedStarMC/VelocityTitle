package top.redstarmc.plugin.velocitytitle.velocity.command.title;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import top.redstarmc.plugin.velocitytitle.velocity.command.VelocityTitleCommand;
import top.redstarmc.plugin.velocitytitle.velocity.record.Title;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class ListBuilder implements VelocityTitleCommand {

    /**
     * 子命令树
     *
     * @return 直接通过添加到 then() 添加的命令树
     */
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("list")
                .requires(source
                        -> source.hasPermission("velocitytitle.title*")
                        || source.hasPermission("velocitytitle.title.list")
                        || source.hasPermission("velocitytitle.admin")
                )
                .executes(context -> {

                    execute(context.getSource());

                    return 1;
                });
    }

    void execute(CommandSource source){
        ArrayList<Title> titles = new ArrayList<Title>();

        //TODO 查询 展示

        for (Title title : titles){
            source.sendMessage(text(title.name()));
        }

    }

}
