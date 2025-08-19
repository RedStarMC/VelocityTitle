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

public enum PrefixDictionary implements SQLTable {
    PREFIX_DICTIONARY((table) -> {
        table.addAutoIncrementColumn("id", NumberType.INT, true, true);
        table.addColumn("name", "VARCHAR(256) NOT NULL"); // 作为索引
        table.addColumn("display", "VARCHAR(256) NOT NULL"); // 实际展示
        table.addColumn("description","VARCHAR(256)"); // 描述
        table.setIndex("name", IndexType.UNIQUE_KEY);
    });
    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    private static final String tableName = "PREFIX_DICTIONARY";

    PrefixDictionary(Consumer<TableCreateBuilder> builder) {
        this.builder = builder;
    }

    @Override
    public @Nullable SQLManager getSQLManager() {
        return this.manager;
    }

    @Override
    public @NotNull String getTableName() {
        return tableName;
    }

    @Override
    public boolean create(SQLManager sqlManager) throws SQLException {
        this.manager = sqlManager;

        TableCreateBuilder tableBuilder = sqlManager.createTable(tableName);
        if (builder != null) builder.accept(tableBuilder);

        return tableBuilder.build().executeFunction(l -> l > 0, false);
    }

    public static void initialize(SQLManager sqlManager) throws SQLException {
        PREFIX_DICTIONARY.create(sqlManager);
    }
}
