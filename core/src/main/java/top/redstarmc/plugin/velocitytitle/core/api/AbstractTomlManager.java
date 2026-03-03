/*
 * This file is part of VelocityTitle(https://github.com/RedStarMC/VelocityTitle).
 *
 * Copyright (C) RedStarMC and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package top.redstarmc.plugin.velocitytitle.core.api;

import com.moandjiezana.toml.Toml;
import top.redstarmc.plugin.velocitytitle.core.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * <b>Toml 格式配置文件管理器</b><br>
 * 作为一个抽象类，使用请让子类被继承后实例化。<br>
 * 对于不需要使用的类，重写为空即可。<br>
 */
public abstract class AbstractTomlManager {

    /** 临时标记的版本号 */
    @Deprecated
    public static final String d_version = "0.1.2";

    private final File file;

    private Toml configToml;

    private final String fileName;

    private final File dataFolder;

    /**
     * 构造器
     * @param dataFolder 插件的工作文件夹
     * @param fileName 要操作的配置文件名称
     */
    public AbstractTomlManager(File dataFolder, String fileName){
        this.file = new File(dataFolder,fileName);
        this.dataFolder = dataFolder;
        this.fileName = fileName;
    }

    /**
     * 尝试创建文件（包括父目录），失败则抛出异常。<br>
     * 若成功创建则尝试读取默认配置，若已存在则不进行操作。<br>
     * 但是并不会读取配置文件 读取配置文件请使用 {@link #loadConfig()}
     */
    public void tryCreateFile(){
        if (!dataFolder.exists()) {
            try {
                IOUtils.createDirectory(dataFolder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!file.exists()) {
            try {
                IOUtils.createFile(file);
                injectConfigFromFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 从 Jar包 中的默认配置文件注入到本地配置
     */
    public void injectConfigFromFile(){

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        try {
            IOUtils.copyResource(inputStream, file);
        } catch (IOException e) {
            System.out.println("[VelocityTitle TomlManager] 无法从 Jar 中复制默认配置到文件中");
            throw new RuntimeException(e);
        }

    }

    /**
     * <b>更新配置文件</b><br>
     * 当配置文件版本不一致时，更新配置文件并备份原配置文件。
     */
    public void updateFile(){
        String readVersion = configToml.getString("version");

        if (Objects.equals(readVersion, d_version)){
            return;
        }

        try {
            IOUtils.backupFile(file);  //备份
        } catch (IOException e) {
            System.out.println("[VelocityTitle TomlManager] 无法备份配置文件，请手动备份后重试"+e.getMessage());
            throw new RuntimeException(e);
        }

        IOUtils.delFile(file);

        injectConfigFromFile();

        loadConfig();
    }

    /**
     * <b>读取配置</b><br>
     * 适用于启动、重载时加载配置文件，会从系统重新读取文件
     */
    public void loadConfig(){
        try {
            configToml = IOUtils.readToml(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化配置文件
     */
    public void init(){
        tryCreateFile();

        loadConfig();

        updateFile();

    }

    /**
     * @return {@link File} 格式的配置文件
     */
    public final File getFile() {
        return file;
    }

    /**
     * @return {@link Toml} 格式的配置文件
     */
    public final Toml getConfigToml() {
        return configToml;
    }

}
