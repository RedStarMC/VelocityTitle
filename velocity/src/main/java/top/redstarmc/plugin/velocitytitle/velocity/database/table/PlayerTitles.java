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

public enum PlayerTitles implements SQLTable {
    PLAYER_TITLES((table) -> {
        table.addAutoIncrementColumn("id", NumberType.INT, true, true);
        table.addColumn("player_uuid", "VARCHAR(38) NOT NULL");
        table.addColumn("title_type", "ENUM(prefix,suffix) NOT NULL");
        table.addColumn("title_name", "VARCHAR(256) NOT NULL");

        table.setIndex(IndexType.INDEX, "player_idx", "player_uuid");
        // 唯一约束：防止玩家重复拥有同一个称号
        table.setIndex(IndexType.UNIQUE_KEY, "player_title_unique", "player_uuid", "title_id");
    });
    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    private static final String tableName = "PLAYER_TITLES";

    PlayerTitles(Consumer<TableCreateBuilder> builder) {
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
        PLAYER_TITLES.create(sqlManager);
    }
}
