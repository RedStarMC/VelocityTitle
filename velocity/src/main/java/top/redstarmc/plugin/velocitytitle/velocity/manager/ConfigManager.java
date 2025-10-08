package top.redstarmc.plugin.velocitytitle.velocity.manager;

import top.redstarmc.plugin.velocitytitle.core.api.AbstractTomlManager;

import java.io.File;

public class ConfigManager extends AbstractTomlManager {

    /**
     * 构造器
     * @param dataFolder 插件的工作文件夹
     * @param fileName   要操作的配置文件名称
     */
    public ConfigManager(File dataFolder, String fileName) {
        super(dataFolder, fileName);
    }

    /**
     * Velocity 侧初始化配置文件
     */
    @Override
    public void init() {
        tryCreateFile();

        loadConfig();

        updateFile();

    }


}
