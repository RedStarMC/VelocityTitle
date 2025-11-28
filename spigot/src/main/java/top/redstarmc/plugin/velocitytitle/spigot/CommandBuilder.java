package top.redstarmc.plugin.velocitytitle.spigot;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import top.redstarmc.plugin.velocitytitle.spigot.manager.ConfigManager;

import static net.kyori.adventure.text.Component.text;

public class CommandBuilder {

    ConfigManager language = VelocityTitleSpigot.getInstance().getLanguage();

    public LiteralArgumentBuilder<CommandSourceStack> init(){
        return LiteralArgumentBuilder.<CommandSourceStack>literal("VelocityTitleSpigot")
                .executes(context -> {
                    context.getSource().getSender().sendMessage(
                            text(language.getConfigToml().getString("command.root"))
                                    .append(text(language.getConfigToml().getString("command.success")))
                    );
                    return 1;
                })
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("gui")
                        .executes(context -> {

                            CommandSourceStack source = context.getSource();

                            if ( !( source instanceof Player player)){
                                source.getSender().sendMessage(text(language.getConfigToml().getString("command.console")));
                                return 1;
                            }

                            player.sendMessage("Sorry");

                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("help")
                        .executes(context -> {
                            context.getSource().getSender().sendMessage(
                                    text(language.getConfigToml().getString("command.root"))
                                            .append(text(language.getConfigToml().getString("command.help")))
                                            .append(text(language.getConfigToml().getString("command.gui")))
                            );
                            return 1;
                        })
                );
    }

}
