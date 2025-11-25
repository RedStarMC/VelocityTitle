package top.redstarmc.plugin.velocitytitle.velocity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>文本序列号器</h1>
 */
public final class TextSer {

    /**
     * 将 legacy 格式的字符串转为现代的 {@link Component} 消息组件
     * @param text legacy 格式的字符串
     * @return {@link Component} 组件
     */
    public static @NotNull Component legToCom(String text){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

}
