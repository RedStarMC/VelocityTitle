package top.redstarmc.plugin.velocitytitle.core.util;

import com.moandjiezana.toml.Toml;

import java.io.*;
import java.nio.file.Files;

public class IOUtils {

    /**
     * ����Ŀ¼��������Ŀ¼��
     * @param dir Ŀ��Ŀ¼
     * @throws IOException Ŀ¼����ʧ��ʱ�׳�
     */
    public static void createDirectory(File dir) throws IOException {
        if (dir != null && !dir.exists() && !Files.createDirectories(dir.toPath()).toFile().exists()) {
            throw new IOException("[VelocityTitle Config Loading...] Ŀ¼����ʧ��: " + dir.getAbsolutePath());
        }
    }

    /**
     * �����ļ�����ȷ����Ŀ¼���ڣ�
     * @param file Ŀ���ļ�
     * @throws IOException �ļ�����ʧ��ʱ�׳�
     */
    public static void createFile(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("[VelocityTitle Config Loading...] �ļ�����ʧ��: " + file.getName());
        }
    }

    /**
     * �����������Ƶ��ļ�
     * @param inputStream Դ������
     * @param targetFile Ŀ���ļ�
     * @throws IOException ������ʧ��ʱ�׳�
     */
    public static void copyResource(InputStream inputStream, File targetFile) throws IOException {
        if (inputStream == null) {
            throw new FileNotFoundException("[VelocityTitle Config Loading...] ��Դ������Ϊ��: " + targetFile.getName());
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
     * �����ļ���������Ϊԭ�ļ���+.old��
     * @param sourceFile Դ�ļ�
     * @throws IOException ����ʧ��ʱ�׳�
     */
    public static void backupFile(File sourceFile) throws IOException {
        File backupFile = new File(sourceFile.getParent(), sourceFile.getName() + ".old");
        if (!sourceFile.renameTo(backupFile)) {
            throw new IOException("[VelocityTitle Config Loading...] �ļ�����ʧ��: " + backupFile.getName());
        }
    }

    /**
     * ��ȡTOML�����ļ�
     * @param file �����ļ�
     * @return Toml����
     * @throws FileNotFoundException ��ȡ�����ʧ��ʱ�׳�
     */
    public static Toml readToml(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("[VelocityTitle Config Loading...] �����ļ�������: " + file.getName());
        }
        return new Toml().read(file);
    }

    /**
     * ɾ���ļ�
     * @param file �ļ�
     * @return �Ƿ�ɹ�
     */
    public static boolean delFile(File file){
        return file.delete();
    }
}
