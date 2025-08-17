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
     * 以指定的 {@link SQLManager} 实例初始化并创建该表
     *
     * @param sqlManager {@link SQLManager} 实例
     * @return 是否新创建了本表 (若已创建或创建失败则返回false)
     * @throws SQLException 当数据库返回异常时抛出
     */
    @Override
    public boolean create(SQLManager sqlManager) throws SQLException {
        return false;
    }

    /**
     * 得到 {@link #create(SQLManager)} 用于初始化本实例的 {@link SQLManager} 实例
     *
     * @return {@link SQLManager} 实例
     */
    @Override
    public @Nullable SQLManager getSQLManager() {
        return null;
    }

    /**
     * 得到本表表名，不得为空。
     *
     * @return 本表表名
     */
    @Override
    public @NotNull String getTableName() {
        return null;
    }
}
