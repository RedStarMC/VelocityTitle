package top.redstarmc.plugin.velocitytitle.velocity.database.table;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.enums.NumberType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.function.Consumer;

public enum Title implements SQLTable {
    USER((table) -> {
        table.addAutoIncrementColumn("id", NumberType.INT, true, true);
        table.addColumn("uuid", "VARCHAR(32) NOT NULL UNIQUE KEY");
        table.addColumn("username", "VARCHAR(16) NOT NULL");
        table.addColumn("age", "TINYINT NOT NULL DEFAULT 1");
        table.addColumn("email", "VARCHAR(32)");
        table.addColumn("phone", "VARCHAR(16)");
        table.addColumn("registerTime", "DATETIME NOT NULL");
        table.setIndex("username", IndexType.UNIQUE_KEY); // ���Ψһ����
        table.setIndex(IndexType.INDEX, "contact", "email", "phone"); //����������� (ʾ��)
    });

    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    Title(Consumer<TableCreateBuilder> builder) {
        this.builder = builder;
    }

    @Override
    public @Nullable SQLManager getSQLManager() {
        return this.manager;
    }

    @Override
    public @NotNull String getTableName() {
        return null;
    }

    @Override
    public boolean create(SQLManager sqlManager) throws SQLException {
        return false;
    }

}
