package top.redstarmc.plugin.velocitytitle.velocity.database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

@Deprecated
public enum Tables implements SQLTable {
    ;

    /**
     * ��ָ���� {@link SQLManager} ʵ����ʼ���������ñ�
     *
     * @param sqlManager {@link SQLManager} ʵ��
     * @return �Ƿ��´����˱��� (���Ѵ����򴴽�ʧ���򷵻�false)
     * @throws SQLException �����ݿⷵ���쳣ʱ�׳�
     */
    @Override
    public boolean create(SQLManager sqlManager) throws SQLException {
        return false;
    }

    /**
     * �õ� {@link #create(SQLManager)} ���ڳ�ʼ����ʵ���� {@link SQLManager} ʵ��
     *
     * @return {@link SQLManager} ʵ��
     */
    @Override
    public @Nullable SQLManager getSQLManager() {
        return null;
    }

    /**
     * �õ��������������Ϊ�ա�
     *
     * @return �������
     */
    @Override
    public @NotNull String getTableName() {
        return null;
    }
}
