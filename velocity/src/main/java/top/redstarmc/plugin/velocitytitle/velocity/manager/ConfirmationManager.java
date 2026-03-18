/*
 * This file is part of VelocityTitle(https://github.com/RedStarMC/VelocityTitle).
 *
 * Copyright (C) RedStarMC and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package top.redstarmc.plugin.velocitytitle.velocity.manager;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.CommandInfo;
import top.redstarmc.plugin.velocitytitle.velocity.configuration.Language;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmationManager {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private final Map<UUID, PendingAction> pendingActions;
    private final Language language;
    private final long expired;
    private final boolean enabled;

    public ConfirmationManager(Language language, long expired, boolean enabled) {
        this.language = language;
        this.expired = expired;
        this.enabled = enabled;
        this.pendingActions = new ConcurrentHashMap<>();
    }

    public record PendingAction(
            Runnable action, // 要执行的操作
            String note, // 操作备注
            long createTime // 创建时间
    ) {

    }

    /** 添加一个操作 */
    public void putAction(@NotNull CommandSource source, @Nullable String note, @NotNull Runnable action) {
        if ( ! enabled ) {
            action.run();
            return;
        }

        UUID uuid = getSourceId(source);

        pendingActions.put(uuid, new PendingAction(action, note, System.currentTimeMillis()));

        source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.put"), note, expired));
    }

    /** 执行操作 */
    public void confirmAction(CommandSource source) {
        if ( ! enabled ) {
            source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.disable")));
            return;
        }

        UUID uuid = getSourceId(source);
        PendingAction action = pendingActions.remove(uuid);

        if ( action == null ) {
            source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.empty")));
            return;
        }

        if ( System.currentTimeMillis() - action.createTime > expired * 1000 ) {
            source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.expired")));
            return;
        }

        action.action.run();
        source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.run"), action.note));
    }

    /** 取消操作 */
    public void cancel(CommandSource source) {
        if ( ! enabled ) {
            source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.disable")));
            return;
        }

        UUID uuid = getSourceId(source);
        boolean result = pendingActions.remove(uuid) != null;
        if ( result ) {
            source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.cancel")));
        } else {
            source.sendMessage(TextSer.legToCom(CommandInfo.prefix() + language.getConfigToml().getString("commands.confirm.empty")));
        }
    }

    private UUID getSourceId(CommandSource source) {
        if ( source instanceof Player player ) {
            return player.getUniqueId();
        }
        return CONSOLE_UUID;
    }

}
