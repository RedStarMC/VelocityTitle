package top.redstarmc.plugin.velocitytitle.velocity.util;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;

import java.util.UUID;

/**
 * <h1>格式转换器</h1>
 */
public final class FormatConversion {

    /**
     * @param uuid 玩家的 {@link UUID}
     * @return {@link Player} 实例
     */
    public static @Nullable Player uuidToPlayer(UUID uuid){
        return VelocityTitleVelocity.getInstance().getServer().getPlayer(uuid).orElse(null);
    }

    /**
     * @param player_name 玩家名称
     * @return {@link Player} 玩家实例
     */
    public static @Nullable Player nameToPlayer(@NotNull String player_name){
        return VelocityTitleVelocity.getInstance().getServer().getPlayer(player_name).orElse(null);
    }

    /**
     * @param player_name 玩家名称
     * @return 玩家的 {@link UUID}
     */
    public static @Nullable UUID nameToUUID(String player_name){
        Player player = VelocityTitleVelocity.getInstance().getServer().getPlayer(player_name).orElse(null);
        if (player != null) return player.getUniqueId();
        else return null;
    }

}
