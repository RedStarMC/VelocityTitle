package top.redstarmc.plugin.velocitytitle.velocity.util;

import top.redstarmc.plugin.velocitytitle.core.api.AbstractLoggerManager;

/**
 * <h1>日志管理器</h1>
 * 提供了日志相关的操作代码，以免重复编写发送日志的操作。
 */
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
            System.out.println(ColoredConsole.toANSI(s + "§r"));
        }
    }

}
