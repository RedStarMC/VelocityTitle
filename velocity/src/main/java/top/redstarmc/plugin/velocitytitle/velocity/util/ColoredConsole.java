package top.redstarmc.plugin.velocitytitle.velocity.util;

import java.util.LinkedHashMap;

/**
 * <h1>Velocity 控制台染色器</h1>
 */
public final class ColoredConsole {

    private static final LinkedHashMap<String, String> MAP = new LinkedHashMap<>();

    static {
        MAP.put("§0", "\u001B[30m");
        MAP.put("§1", "\u001B[34m");
        MAP.put("§2", "\u001B[32m");
        MAP.put("§3", "\u001B[36m");
        MAP.put("§4", "\u001B[31m");
        MAP.put("§5", "\u001B[35m");
        MAP.put("§6", "\u001B[33m");
        MAP.put("§7", "\u001B[37m");
        MAP.put("§8", "\u001B[90m");
        MAP.put("§9", "\u001B[94m");
        MAP.put("§a", "\u001B[92m");
        MAP.put("§b", "\u001B[96m");
        MAP.put("§c", "\u001B[91m");
        MAP.put("§d", "\u001B[95m");
        MAP.put("§e", "\u001B[93m");
        MAP.put("§f", "\u001B[97m");
        MAP.put("§k", "\u001B[5m");
        MAP.put("§l", "\u001B[1m");
        MAP.put("§m", "\u001B[9m");
        MAP.put("§n", "\u001B[4m");
        MAP.put("§o", "\u001B[3m");
        MAP.put("§r", "\u001B[0m");
    }

    public static String toANSI(String s) {
        final String[] out = {s};
        MAP.forEach((mc, ansi) -> out[0] = out[0].replace(mc, ansi));
        return out[0];
    }

}
