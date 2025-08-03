package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;

import java.io.File;

public class Language extends ConfigManager {
    static final String fileName = "language-velocity.toml";
    public Language(File dataFolder) {
        super(dataFolder, fileName);
    }

}
