package top.redstarmc.plugin.velocitytitle.velocity.configuration;

import com.moandjiezana.toml.Toml;

import com.moandjiezana.toml.TomlWriter;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.moandjiezana.toml.Toml.*;

public class TomlManager {

    private static final File path = VelocityTitleVelocity.getInstance().getDataFolder();

    private static final File configFile = new File(path,"config.toml");

    private static final File languageFile = new File(path,"language.toml");

    Toml configToml;

    Toml languageToml;

    public void init(){
        // 确保文件存在
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

        // 读取配置，检查配置版本



//            // 从资源文件夹中读取标准配置文件
//            InputStream inputStream = TomlManager.class.getResourceAsStream("/config.toml");

    }






}
