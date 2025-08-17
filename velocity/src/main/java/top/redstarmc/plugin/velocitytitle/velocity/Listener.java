package top.redstarmc.plugin.velocitytitle.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.database.operate.PlayerWearOperate;
import top.redstarmc.plugin.velocitytitle.velocity.manager.EasySQLManager;

/**
 * <h1>���������</h1>
 */
public class Listener {

    /**
     * ����ʱ����UUID���Ա�����ʱ���顣
     * ��ʵ��Ϊ���������߷�����
     * @param event �����ӷ��¼�
     */
    @Subscribe
    public void onServerPostConnectEvent(@NotNull ServerPostConnectEvent event){
        Player player_tmp = event.getPlayer();
        String name = player_tmp.getUsername();
        Player player = VelocityTitleVelocity.getInstance().getServer().getPlayer(name).orElse(null);

        if (player == null) return;

        PlayerWearOperate.ReplaceUUID(EasySQLManager.getSqlManager(), player.getUniqueId().toString(), name);
    }


}
