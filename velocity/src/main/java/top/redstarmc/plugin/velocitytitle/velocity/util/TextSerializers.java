package top.redstarmc.plugin.velocitytitle.velocity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * <h1>ÎÄ±¾ÐòÁÐºÅÆ÷</h1>
 */
public class TextSerializers {

    public static Component legacyToComponent(String text){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

}
