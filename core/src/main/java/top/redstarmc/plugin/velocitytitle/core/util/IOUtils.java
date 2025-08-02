package top.redstarmc.plugin.velocitytitle.core.util;

import com.moandjiezana.toml.Toml;

import java.io.*;
import java.nio.file.Files;

public class IOUtils {

    /**
     * 创建目录（包括父目录）
     * @param dir 目标目录
     * @throws IOException 目录创建失败时抛出
     */
    public static void createDirectory(File dir) throws IOException {
        if (dir != null && !dir.exists() && !Files.createDirectories(dir.toPath()).toFile().exists()) {
            throw new IOException("[VelocityTitle Config Loading...] 目录创建失败: " + dir.getAbsolutePath());
        }
    }

    /**
     * 创建文件（需确保父目录存在）
     * @param file 目标文件
     * @throws IOException 文件创建失败时抛出
     */
    public static void createFile(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("[VelocityTitle Config Loading...] 文件创建失败: " + file.getName());
        }
    }

    /**
     * 从输入流复制到文件
     * @param inputStream 源输入流
     * @param targetFile 目标文件
     * @throws IOException 流操作失败时抛出
     */
    public static void copyResource(InputStream inputStream, File targetFile) throws IOException {
        if (inputStream == null) {
            throw new FileNotFoundException("[VelocityTitle Config Loading...] 资源输入流为空: " + targetFile.getName());
        }
        try (InputStream is = inputStream;
             OutputStream os = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

    /**
     * 备份文件（重命名为原文件名+.old）
     * @param sourceFile 源文件
     * @throws IOException 备份失败时抛出
     */
    public static void backupFile(File sourceFile) throws IOException {
        File backupFile = new File(sourceFile.getParent(), sourceFile.getName() + ".old");
        if (!sourceFile.renameTo(backupFile)) {
            throw new IOException("[VelocityTitle Config Loading...] 文件备份失败: " + backupFile.getName());
        }
    }

    /**
     * 读取TOML配置文件
     * @param file 配置文件
     * @return Toml对象
     * @throws FileNotFoundException 读取或解析失败时抛出
     */
    public static Toml readToml(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("[VelocityTitle Config Loading...] 配置文件不存在: " + file.getName());
        }
        return new Toml().read(file);
    }

    /**
     * 删除文件
     * @param file 文件
     * @return 是否成功
     */
    public static boolean delFile(File file){
        return file.delete();
    }
}
