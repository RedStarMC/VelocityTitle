/*
 * This file is part of VelocityTitle(https://github.com/RedStarMC/VelocityTitle).
 *
 * Copyright (C) RedStarMC and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package top.redstarmc.plugin.velocitytitle.velocity.database.table;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.ForeignKeyRule;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.enums.NumberType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * 存储玩家当前展示的称号
 */
public enum PlayerWear implements SQLTable {
    PLAYER_WEAR((table) -> {
        table.addAutoIncrementColumn("id", NumberType.INT, true, true);
        table.addColumn("player_uuid", "VARCHAR(38) NOT NULL");
        table.addColumn("player_name", "VARCHAR(64)");
        table.addColumn("prefix", "VARCHAR(256)");
        table.addColumn("suffix", "VARCHAR(256)");

        table.setIndex(IndexType.UNIQUE_KEY, "uk_uuid", "player_uuid");

        table.addForeignKey("prefix", null, TitleDictionary.tableName, "id", ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE);
        table.addForeignKey("suffix", null, TitleDictionary.tableName, "id", ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE);

    });

    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    private static final String tableName = "PLAYER_WEAR";

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
