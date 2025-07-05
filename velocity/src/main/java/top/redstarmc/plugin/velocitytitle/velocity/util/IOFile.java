package top.redstarmc.plugin.velocitytitle.velocity.util;

import java.io.*;

public class IOFile {
    public static void reSave(InputStream inputStream, File outputFile) {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            if (inputStream == null) {
                throw new FileNotFoundException("[VelocityTitle] Jar 已损坏！ config not found in resources");
            }

            byte[] buffer = new byte[4096]; // 缓冲区大小
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush(); // 刷新缓冲区
        } catch (IOException e) {
            System.out.println("[VelocityTitle]"+e.getMessage());
            e.printStackTrace(); // 捕获异常并打印堆栈信息
        }
    }
}
