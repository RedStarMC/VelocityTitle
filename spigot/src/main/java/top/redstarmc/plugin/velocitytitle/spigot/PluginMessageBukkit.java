package top.redstarmc.plugin.velocitytitle.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.core.util.MessageReader;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PluginMessageBukkit implements PluginMessageListener{
    private final String incomingChannel;
    private final String outgoingChannel;
    private final ExecutorService executor;
    private final VelocityTitleSpigot plugin;

    public PluginMessageBukkit(ExecutorService executor, VelocityTitleSpigot server) {
        this.plugin = server;
        this.incomingChannel = "velocitytitle:server";
        this.outgoingChannel = "velocitytitle:proxy";
        this.executor = executor;
        registerChannels();
    }

    private void registerChannels() {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, outgoingChannel)){
            Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, outgoingChannel);
        }
        if (!Bukkit.getMessenger().isIncomingChannelRegistered(plugin, incomingChannel)){
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, incomingChannel , this);
        }
    }

    // ������Ϣ������
    public Future<?> sendMessage(PluginMessageRecipient recipient, String... data) {
        return executor.submit(() -> {
            try {
                byte[][] messages = MessageReader.buildMessage(data);
                for (byte[] msg : messages) {
                    recipient.sendPluginMessage(plugin, outgoingChannel, msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // ������յ�����Ϣ
    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals(incomingChannel)) {
            return;
        }
        try {
            MessageReader reader = MessageReader.read(message);
            if (reader.isCompleted()) {
                execute(reader.build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ��Ϣִ���߼�
    public void execute(String[] data) {
        System.out.println("======================�յ��˲����Ϣ");
//        switch (data[0]) {
//            case "SendPrivateRaw":
//                // ������˽��
//                String to = data[1];
//                String from = data[2];
//                String rawJson = data[3];
//                String fallback = data.length > 4 ? data[4] : "";
//                Component message = Components.parseRaw(rawJson);
//                Player target = Bukkit.getPlayer(to);
//                if (target != null) {
//                    target.sendMessage(message);
//                }
//                break;
//            case "BroadcastRaw":
//                // �������ת���Ĺ㲥
//                String uuid = data[1];
//                String raw = data[2];
//                String perm = data[3];
//                List<Integer> ports = data[5].isEmpty() ? List.of() :
//                        Arrays.stream(data[5].split(";")).map(Integer::parseInt).toList();
//                Component broadcastMsg = Components.parseRaw(raw);
//                // ֻ�������˿ڷ�����������Ϣ
//                if (ports.isEmpty() || ports.contains(BukkitProxyManager.getPort())) {
//                    Bukkit.getOnlinePlayers().stream()
//                            .filter(p -> perm.isEmpty() || p.hasPermission(perm))
//                            .forEach(p -> p.sendMessage(UUID.fromString(uuid), broadcastMsg));
//                }
//                break;
//            case "GlobalMute":
//                // ����ȫ�ֽ���
//                TrChatBukkit.setGlobalMuting("on".equals(data[1]));
//                break;
//        }
    }


}
