package top.redstarmc.plugin.velocitytitle.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class PluginMessage {

    /**
     * ����ƺ��б�
     */
    public static final MinecraftChannelIdentifier TITLE_LIST = MinecraftChannelIdentifier.from("velocitytitle:list");

    /**
     * ������ҵĳƺ��б�
     */
    public static final MinecraftChannelIdentifier PLAYER_TITLE = MinecraftChannelIdentifier.from("velocitytitle:player-title");

    /**
     * ������ҵ�ǰ����ĳƺ�
     */
    public static final MinecraftChannelIdentifier PLAYER_WEAR = MinecraftChannelIdentifier.from("velocitytitle:player-wear");

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        // ������Դ��Σ����ȼ���ʶ���Ƿ�ƥ�䡣
        // ������������Ϣ����Ϊ IDENTIFIER as handled��
        // ��ֹת���κ�Դ�Կͻ��˵��ʼ���
        if (!TITLE_LIST.equals(event.getIdentifier())) {
            return;
        }

        // �� PluginMessage ���Ϊ�Ѵ�����ʾ����
        // ��Ӧת������ԭʼĿ�ĵء�
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        // ���ߣ�

        // �� PluginMessage ���Ϊ��ת������ʾ����
        // Ӧ��ͨ�����ͺ����ٶȲ�����һ����
        //
        // ʹ��ʱӦ����С�ģ�
        // ��Ϊ�κοͻ��˶��������ɷ����κ�����Ҫ�Ķ�������װ�Ǵ���
        // event.setResult(PluginMessageEvent.ForwardResult.forward());

        // ����Դ�Ǻ�˷�����ʱ�ų��Խ�������
        if (!(event.getSource() instanceof ServerConnection backend)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        // �������ݰ�����

        if (in.readUTF().equals("Hello Velocity!")){
            System.out.println("Hello Velocity!");
            System.out.println("===========================================================================");
        }

    }
}
