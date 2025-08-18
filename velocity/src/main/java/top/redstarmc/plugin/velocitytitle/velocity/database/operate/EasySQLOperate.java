package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

public interface EasySQLOperate {

    public static LoggerManager logger = VelocityTitleVelocity.getInstance().getLogger();

    public static ConfigManager language = VelocityTitleVelocity.getInstance().getLanguage();

}
