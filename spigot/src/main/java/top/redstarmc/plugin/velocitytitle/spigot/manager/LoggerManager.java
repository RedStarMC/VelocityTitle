package top.redstarmc.plugin.velocitytitle.spigot.manager;

import org.bukkit.Bukkit;
import top.redstarmc.plugin.velocitytitle.core.api.AbstractLoggerManager;

public class LoggerManager extends AbstractLoggerManager {

    public LoggerManager(String INFO_PREFIX, boolean debugMode) {
        super(INFO_PREFIX, debugMode);
    }

    /**
     * <h2>向控制台打印的方法</h2>
     * @param msg 内容
     */
    @Override
    public void sendMessage(String... msg) {
        for (String s : msg) {
            if (s == null) continue;
            Bukkit.getConsoleSender().sendMessage(s);
        }
    }
}
