package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import com.moandjiezana.toml.Toml;
import top.redstarmc.plugin.velocitytitle.velocity.util.IOFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TomlManager {

    private final File configFile;

    private final File languageFile;

    @Deprecated
    private static final String d_version = "0.1.1";

    private Toml configToml;

    private Toml languageToml;

    public TomlManager(File dataFolder) {
        this.configFile = new File(dataFolder, "config-velocity.toml");
        this.languageFile = new File(dataFolder, "language-velocity.toml");
    }



    public void init(){

        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (!languageFile.exists()) {
            try {
                languageFile.getParentFile().mkdirs();
                languageFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        configToml = new Toml().read(configFile);
        languageToml = new Toml().read(languageFile);


        String configVersion = configToml.getString("version");
        if (!Objects.equals(configVersion, d_version)){

            IOFile.reSave(TomlManager.class.getResourceAsStream("/config-velocity.toml"),configFile);

            configToml = new Toml().read(configFile);
        }

        String languageVersion = configToml.getString("version");
        if (!Objects.equals(languageVersion, d_version)){

            IOFile.reSave(TomlManager.class.getResourceAsStream("/language-velocity.toml"),languageFile);

            languageToml = new Toml().read(languageFile);
        }
    }

    public Toml getConfigToml() {
        return configToml;
    }

    public Toml getLanguageToml() {
        return languageToml;
    }
}
