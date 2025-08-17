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

public enum PlayerWear implements SQLTable {
    PLAYER_WEAR((table) -> {
        table.addAutoIncrementColumn("id", NumberType.INT, true, true);
        table.addColumn("uuid", "VARCHAR(38) NOT NULL");
        table.addColumn("name", "VARCHAR(64) NOT NULL");
        table.addColumn("prefix", "VARCHAR(256)");
        table.addColumn("suffix", "VARCHAR(256)");

        table.setIndex("uuid", IndexType.UNIQUE_KEY);
    });

    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    private static final String tableName = "Velocity_UUID";

    PlayerWear(Consumer<TableCreateBuilder> builder) {
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
        PLAYER_WEAR.create(sqlManager);
    }

}
