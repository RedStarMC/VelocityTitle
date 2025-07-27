package top.redstarmc.plugin.velocitytitle.core.util;

import java.io.*;

public class IOUtils {
    public static void reSave(InputStream inputStream, File outputFile) {
        try (InputStream is = inputStream;
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            if (is == null) {
                throw new FileNotFoundException("[VelocityTitle] Jar 已损坏！资源文件不存在: " + outputFile.getName());
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush(); // 刷新缓冲区
        } catch (IOException e) {
            System.out.println("[VelocityTitle]"+e.getMessage());
            e.printStackTrace(); // 捕获异常并打印堆栈信息
        }
    }
}
