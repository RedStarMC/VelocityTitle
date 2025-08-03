package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;

import java.io.File;

public class Config extends ConfigManager {

    static final String fileName = "config-velocity.toml";

    public Config(File dataFolder) {
        super(dataFolder, fileName);
    }

}
