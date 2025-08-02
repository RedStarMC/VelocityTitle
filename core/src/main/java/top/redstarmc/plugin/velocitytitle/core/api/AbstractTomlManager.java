package top.redstarmc.plugin.velocitytitle.core.api;

import com.moandjiezana.toml.Toml;
import top.redstarmc.plugin.velocitytitle.core.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * <h1>Toml ��ʽ�����ļ�������</h1>
 * ��Ϊһ�������࣬ʹ���������౻�̳к�ʵ������
 * ���ڲ���Ҫʹ�õ��࣬��дΪ�ռ��ɡ�
 */
public abstract class AbstractTomlManager {

    @Deprecated
    private static final String d_version = "0.1.1";

    private final File file;

    private Toml configToml;

    private final String fileName;

    private final File dataFolder;

    /**
     * ������
     * @param dataFolder ����Ĺ����ļ���
     * @param fileName Ҫ�����������ļ�����
     */
    public AbstractTomlManager(File dataFolder, String fileName){
        this.file = new File(dataFolder,fileName);
        this.dataFolder = dataFolder;
        this.fileName = fileName;
    }

    /**
     * ���Դ����ļ���������Ŀ¼����ʧ�����׳��쳣��
     * ���ɹ��������Զ�ȡĬ�����ã����Ѵ����򲻽��в�����
     * ���ǲ������ȡ�����ļ� ��ȡ�����ļ���ʹ�� {@link #loadConfig()}
     */
    public void tryCreateFile(){
        if (!dataFolder.exists()) {
            try {
                IOUtils.createDirectory(dataFolder);
            } catch (IOException e) {
                throw new RuntimeException(e); //TODO ���������
            }
        }
        if (!file.exists()) {
            try {
                IOUtils.createFile(file);
                injectConfigFromFile();
            } catch (IOException e) {
                throw new RuntimeException(e); //TODO ���������
            }
        }
    }

    public void injectConfigFromFile(){

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        try {
            IOUtils.copyResource(inputStream, file);
        } catch (IOException e) {
            System.out.println("[VelocityTitle TomlManager] �޷��� Jar �и���Ĭ�����õ��ļ���");
            throw new RuntimeException(e);
        }

    }

    /**
     * <h2>���������ļ�</h2>
     * �������ļ��汾��һ��ʱ�����������ļ�������ԭ�����ļ���
     */
    public void updateFile(){
        String readVersion = configToml.getString("Version");

        if (Objects.equals(readVersion, d_version)){
            return;
        }

        try {
            IOUtils.backupFile(file);  //����
        } catch (IOException e) {
            System.out.println("[VelocityTitle TomlManager] �޷����������ļ������ֶ����ݺ�����"+e.getMessage());
            throw new RuntimeException(e);
        }

        IOUtils.delFile(file);

        injectConfigFromFile();

    }

    /**
     * <h2>��ȡ����</h2>
     * ����������������ʱ���������ļ������ϵͳ���¶�ȡ�ļ�
     */
    public void loadConfig(){
        try {
            configToml = IOUtils.readToml(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO �޸����
        }
    }

    /**
     * <h2>��ʼ�������ļ�</h2>
     * ������д����
     */
    public void init(){
        tryCreateFile();

        loadConfig();

        updateFile();

        loadConfig();

    }

    public final File getFile() {
        return file;
    }

    public final Toml getConfigToml() {
        return configToml;
    }
}
