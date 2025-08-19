package org.example.flashytitles.fabric.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * FlashyTitles 称号同步数据包
 */
public record TitleSyncPayload(String action, String titleId, String titleText, boolean animated) implements CustomPayload {
    
    public static final Identifier TITLE_SYNC_PAYLOAD_ID = Identifier.of("flashytitles", "title_sync");
    public static final CustomPayload.Id<TitleSyncPayload> ID = new CustomPayload.Id<>(TITLE_SYNC_PAYLOAD_ID);
    
    public static final PacketCodec<RegistryByteBuf, TitleSyncPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, TitleSyncPayload::action,
        PacketCodecs.STRING, TitleSyncPayload::titleId,
        PacketCodecs.STRING, TitleSyncPayload::titleText,
        PacketCodecs.BOOL, TitleSyncPayload::animated,
        TitleSyncPayload::new
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    // 便捷的构造方法
    public static TitleSyncPayload equip(String titleId, String titleText, boolean animated) {
        return new TitleSyncPayload("equip", titleId, titleText, animated);
    }
    
    public static TitleSyncPayload unequip() {
        return new TitleSyncPayload("unequip", "", "", false);
    }
    
    public static TitleSyncPayload heartbeat() {
        return new TitleSyncPayload("heartbeat", "", "", false);
    }
}
