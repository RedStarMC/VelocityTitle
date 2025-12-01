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
 * 存储玩家所拥有的称号
 */
public enum PlayerTitles implements SQLTable {
    PLAYER_TITLES((table) -> {
        table.addAutoIncrementColumn("id", NumberType.INT, true, true);
        table.addColumn("player_uuid", "VARCHAR(38) NOT NULL", "玩家 uuid");
        table.addColumn("title_name", "INT NOT NULL", "称号 id 对应主表的自增主键");
        table.addColumn("time_until","DATETIME NOT NULL", "到期时间，用 DATETIME");

        table.setIndex(IndexType.INDEX, "player_idx", "player_uuid"); // 为玩家uuid设置索引以便 where uuid=''
        table.setIndex(IndexType.UNIQUE_KEY, "uk_title", "player_uuid", "title_name"); // 防止玩家重复拥有同一个称号

        table.addForeignKey("title_name",null, TitleDictionary.tableName, "id", ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE);

    });
    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    public static final String tableName = "PLAYER_TITLES";

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
