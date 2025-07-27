package top.redstarmc.plugin.velocitytitle.core.api;

import com.moandjiezana.toml.Toml;

import java.io.File;

/**
 * <h1>Toml 格式配置文件管理器</h1>
 * 作为一个抽象类，使用请让子类被继承后实例化。
 * 对于不需要使用的类，重写为空即可。
 */
public abstract class AbstractTomlManager {

    @Deprecated
    private static final String d_version = "0.1.1";

    private final File file;

    private Toml configToml;

    public AbstractTomlManager(File dataFolder, String fileName){
        this.file = new File(dataFolder,fileName);
    }

    /**
     * <h2>尝试创建文件</h2>
     * 会检查文件是否存在，若不存在则创建
     */
    public void tryCreateFile(){
        //TODO
    }

    /**
     * <h2>更新配置文件</h2>
     * 当配置文件版本不一致时，更新配置文件并备份原配置文件。
     */
    public void updateFile(){
        //TODO
    }

    /**
     * <h2>重新读取</h2>
     * 适用于重载配置文件，会从系统重新读取文件
     */
    public void reLoad(){
        //TODO
    }

    /**
     * <h2>初始化配置文件</h2>
     * 抽象类，子类重写即可
     */
    public abstract void init();

    public final File getFile() {
        return file;
    }

    public final Toml getConfigToml() {
        return configToml;
    }
}
