package top.redstarmc.plugin.velocitytitle.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

public class ReloadBuilder implements VelocityTitleCommand {

    //TODO 暂不确定是否能正常运行
    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal("reload")
                .requires(source -> source.hasPermission("velocitytitle.reload"))
                .executes(context -> {

                    VelocityTitleVelocity.getInstance().onProxyReload(new ProxyReloadEvent());

                    context.getSource().sendMessage(TextSer.legToCom(
                            language.getConfigToml().getString("logs.reload")
                    ));

                    return Command.SINGLE_SUCCESS;
                });
    }

}
