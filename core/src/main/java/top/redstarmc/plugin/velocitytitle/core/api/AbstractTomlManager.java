package top.redstarmc.plugin.velocitytitle.core.api;

import com.moandjiezana.toml.Toml;

import java.io.File;

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

    public AbstractTomlManager(File dataFolder, String fileName){
        this.file = new File(dataFolder,fileName);
    }

    /**
     * <h2>���Դ����ļ�</h2>
     * �����ļ��Ƿ���ڣ����������򴴽�
     */
    public void tryCreateFile(){
        //TODO
    }

    /**
     * <h2>���������ļ�</h2>
     * �������ļ��汾��һ��ʱ�����������ļ�������ԭ�����ļ���
     */
    public void updateFile(){
        //TODO
    }

    /**
     * <h2>���¶�ȡ</h2>
     * ���������������ļ������ϵͳ���¶�ȡ�ļ�
     */
    public void reLoad(){
        //TODO
    }

    /**
     * <h2>��ʼ�������ļ�</h2>
     * �����࣬������д����
     */
    public abstract void init();

    public final File getFile() {
        return file;
    }

    public final Toml getConfigToml() {
        return configToml;
    }
}
