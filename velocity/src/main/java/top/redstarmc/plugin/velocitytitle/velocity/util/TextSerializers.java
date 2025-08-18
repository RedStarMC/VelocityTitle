package top.redstarmc.plugin.velocitytitle.velocity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * <h1>文本序列号器</h1>
 */
public class TextSerializers {

    public static Component legacyToComponent(String text){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

}
