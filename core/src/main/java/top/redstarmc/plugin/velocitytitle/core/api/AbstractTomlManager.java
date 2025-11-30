/*
 * This file is part of VelocityTitle.
 *
 * Copyright (C) RedStarMC, pingguomc and contributors
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
 * <h1>Toml 格式配置文件管理器</h1>
 * 作为一个抽象类，使用请让子类被继承后实例化。
 * 对于不需要使用的类，重写为空即可。
 */
public abstract class AbstractTomlManager {

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
     * 尝试创建文件（包括父目录），失败则抛出异常。
     * 若成功创建则尝试读取默认配置，若已存在则不进行操作。
     * 但是并不会读取配置文件 读取配置文件请使用 {@link #loadConfig()}
     */
    public void tryCreateFile(){
        if (!dataFolder.exists()) {
            try {
                IOUtils.createDirectory(dataFolder);
            } catch (IOException e) {
                throw new RuntimeException(e); //TODO 输出待测试
            }
        }
        if (!file.exists()) {
            try {
                IOUtils.createFile(file);
                injectConfigFromFile();
            } catch (IOException e) {
                throw new RuntimeException(e); //TODO 输出待测试
            }
        }
    }

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
     * <h2>更新配置文件</h2>
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
     * <h2>读取配置</h2>
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
     * <h2>初始化配置文件</h2>
     * 子类重写即可
     */
    public void init(){
        tryCreateFile();

        loadConfig();

        updateFile();

    }

    public final File getFile() {
        return file;
    }

    public final Toml getConfigToml() {
        return configToml;
    }

}
