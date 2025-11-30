/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import net.kyori.adventure.text.Component;
import top.redstarmc.plugin.velocitytitle.core.api.AbstractTomlManager;
import top.redstarmc.plugin.velocitytitle.core.util.toStrings;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

public class CommandHelp {

    private static ConfigManager language;

    private static final String sharp = "&9>&r ";

    private static final String next = "\n";

    public static void init(){
        language = VelocityTitleVelocity.getInstance().getLanguage();
    }

    private static String prefix(){
        return language.getConfigToml().getString("commands.prefix");
    }

    private static String head(){
        return toStrings.format(language.getConfigToml().getString("commands.head"), AbstractTomlManager.d_version);
    }








     public static Component root(){
        return TextSer.legToCom(
                prefix() + head() + next
                + language.getConfigToml().getString("commands.help")
        );
    }

    public static Component help(){
        return TextSer.legToCom(
                prefix() + head() + next
                + sharp + "&a/vt title <create|edit|delete|list>&r" + next
                + sharp + "&a/vt player <divide|revoke|delete|list>&r" + next
                + sharp + "&a/vt bank [player]&r" + next
                + sharp + "&a/vt pick [player]&r" + next
                + sharp + "&a/vt wear <name> [player]&r" + next
                + sharp + "&a/vt reload" + next
                + sharp + "&a/vt meta"
        );
    }

    public static Component title(){
        return TextSer.legToCom(
                prefix() + language.getConfigToml().getString("commands.title") + next
                + sharp + language.getConfigToml().getString("commands.titles.create") + next
                + sharp + language.getConfigToml().getString("commands.titles.edit") + next
                + sharp + language.getConfigToml().getString("commands.titles.delete") + next
                + sharp + language.getConfigToml().getString("commands.titles.list") + next
        );
    }



}
