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

package top.redstarmc.plugin.velocitytitle.velocity.database;

import cc.carm.lib.easysql.api.SQLManager;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerTitles;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerWear;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.TitleDictionary;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;
import top.redstarmc.plugin.velocitytitle.velocity.record.Title;
import top.redstarmc.plugin.velocitytitle.velocity.util.TextSer;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static net.kyori.adventure.text.Component.text;

/**
 * <h1>数据库操作</h1>
 * 各种数据库操作的实现。<br>
 * 使用 {@link SQLManager} 进行各种操作<br>
 * TODO 问题：是否需要异步处理？如果需要，怎么进行异步处理？
 * TODO 错误信息和成功信息反馈
 */
public class DataBaseOperate {

    public static LoggerManager logger = VelocityTitleVelocity.getInstance().getLogger();

    private static ConfigManager getLanguage() {
        return VelocityTitleVelocity.getInstance().getLanguage();
    }

    private static SQLManager getSqlManager() {
        return VelocityTitleVelocity.getInstance().getDBManager().getSqlManager();
    }


    /**
     * 查询一个称号信息
     * @param source 命令发送者
     * @param name 称号识别 ID
     */
    public static @Nullable Title selectTitle(@NotNull CommandSource source, String name){
        AtomicReference<Title> title = null;
        getSqlManager().createQuery() // 创建一个查询
                .inTable(TitleDictionary.TITLE_DICTIONARY.getTableName()) // 指定表名
                .selectColumns("id","name", "display", "description", "type") // 选择列
                .addCondition("name", name)
                .build()/*构建查询体*/.executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if(result.next()){
                                String display = result.getString("display");
                                String description = result.getString("description");
                                int id = result.getInt("id");
                                boolean isPrefix = result.getString("type").equals("suffix");
                                title.set(new Title(id, name, display, description, isPrefix));
                            }
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
        return title.get();
    }


    /**
     * 将一个新称号加入称号库
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param display 实际的展示内容
     * @param description 描述
     * @param isPrefix 是否为前缀
     */
    public static void insertTitle(@NotNull CommandSource source, String name, String display, String description, boolean isPrefix){
        //1.查询该 ID 是否存在
        if (selectTitle(source, name) != null){
            source.sendMessage(TextSer.legToCom(getLanguage().getConfigToml().getString("commands.unknown"))); //不存在
            return;
        }

        //2.插入
        getSqlManager().createInsert(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .setColumnNames("name", "display", "description", "type")
                .setParams(name, display, description, isPrefix ? "prefix" : "suffix")
                .executeAsync(
                        (query) -> {},
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );

    }


    /**
     * 编辑一个称号的信息
     * @param source 命令发送者
     * @param title {@link Title} 要编辑的称号实例
     */
    public static void updateTitle(@NotNull CommandSource source, @NotNull Title title){
        //1.查询该 ID 是否存在
        if (selectTitle(source, title.name()) == null){
            source.sendMessage(TextSer.legToCom(getLanguage().getConfigToml().getString("commands.unknown"))); //不存在
            return;
        }

        //2.更新
        getSqlManager().createUpdate(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .addCondition("name", title.name())
                .setColumnValues("display", title.display())
                .setColumnValues("description", title.description())
                .build().executeAsync(
                        (query) -> {},
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
    }


    /**
     * 从称号库删除一个称号
     * @param source 命令发送者
     * @param name 称号识别 ID
     */
    public static void deleteTitle(@NotNull CommandSource source, String name){
        //1.查询该 ID 是否存在
        if (selectTitle(source, name) == null){
            source.sendMessage(TextSer.legToCom(getLanguage().getConfigToml().getString("commands.unknown"))); //不存在
            return;
        }

        //2.删除
        getSqlManager().createDelete(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .addCondition("name", name)
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
    }

    //-------------------------

    /**
     * 查询玩家是否拥有这个称号
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param UUID 玩家 UUID
     * @return 是否拥有
     */
    public static boolean queryTitleOfPlayer(@NotNull CommandSource source, String name, String UUID){
        //1.查询这个称号是否存在以及获得他的ID
        AtomicInteger titleId = new AtomicInteger(-1);
        getSqlManager().createQuery()
                .inTable(TitleDictionary.tableName)
                .selectColumns("id")
                .addCondition("name", name)
                .build()
                .executeAsync(
                        query -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                titleId.set(result.getInt("id"));
                            }
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );

        if(titleId.get() == -1){
            source.sendMessage(TextSer.legToCom(getLanguage().getConfigToml().getString("commands.unknown")));
            return false;
        }
        //2.查询玩家是否拥有
        AtomicBoolean rs = new AtomicBoolean(false);
        getSqlManager().createQuery()
                .inTable(PlayerTitles.tableName)
                .selectColumns("title_name", "player_uuid")
                .addCondition("title_name", name)
                .addCondition("player_uuid", UUID)
                .build()
                .executeAsync(
                        query -> {
                            ResultSet result = query.getResultSet();
                            if (result != null) rs.set(true);
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
        return rs.get();
    }

    /**
     * 分配称号给玩家
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param player_uuid 要给的玩家的 uuid
     */
    public static void divideTitleToPlayer(@NotNull CommandSource source, String name, String player_uuid){
        // 1. 先查询称号在TitleDictionary中的ID（外键依赖）
        AtomicInteger titleId = new AtomicInteger(-1);
        getSqlManager().createQuery()
                .inTable(TitleDictionary.tableName)
                .selectColumns("id")
                .addCondition("name", name)
                .build()
                .executeAsync(
                        query -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                titleId.set(result.getInt("id"));
                            }
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );

        // 2. 若称号存在，插入到PlayerTitles（外键合法）
        if (titleId.get() != -1) {
            getSqlManager().createInsert(PlayerTitles.tableName)
                    .setColumnNames("player_uuid", "title_name", "time_until")
                    .setParams(
                            player_uuid,
                            titleId.get(),
                            0
                    )
                    .executeAsync(
                            query -> {},
                            ((exception, sqlAction) -> {
                                logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                            })
                    );
        } else {
            source.sendMessage(text("a"));
        }

    }


    /**
     * 收回玩家的称号
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param UUID 被执行玩家 UUID
     */
    public static void retrieveTitleFromPlayer(@NotNull CommandSource source, String name, String UUID){
        if(!queryTitleOfPlayer(source, name, UUID)) return;

        getSqlManager().createDelete(PlayerTitles.tableName)
                .addCondition("title_name", name)
                .addCondition("player_uuid", UUID)
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );

        source.sendMessage(text("a"));
    }

    //-------------------------

    /**
     * 穿戴一个称号
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param uuid 被执行玩家 UUID
     */
    public static void playerWearTitle(@NotNull CommandSource source, String name, String uuid) {
        //1.判断称号是否可用
        if (!queryTitleOfPlayer(source, name, uuid)) return;
        //2.查询是前缀还是后缀
        Title title = selectTitle(source, name);
        //3.加入
        getSqlManager().createReplace(PlayerWear.PLAYER_WEAR.getTableName())
                .setColumnNames("player_uuid", title.isPrefix() ? "prefix" : "suffix")
                .setParams(title.isPrefix() ? "prefix" : "suffix", name)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
    }

    /**
     * 查询玩家当前穿戴的称号
     * @param source 命令发送者
     * @param uuid 被执行玩家 UUID
     * @param isPrefix 是否是前缀
     */
    public static Title playerWoreTitle(@NotNull CommandSource source, String uuid, boolean isPrefix){
        AtomicReference<String> name = null;
        getSqlManager().createQuery()
                .inTable(PlayerWear.PLAYER_WEAR.getTableName())
                .selectColumns("player_uuid", isPrefix ? "prefix" : "suffix")
                .addCondition("player_uuid", uuid)
                .build()
                .executeAsync((query) -> {
                            ResultSet result = query.getResultSet();
                            if(result.next()){
                                name.set(result.getString(isPrefix ? "prefix" : "suffix"));
                            }
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
        if(name.get() == null) return null;
        return selectTitle(source, name.get());
    }

    /**
     * 摘除前缀或后缀
     * @param source 命令发送者
     * @param uuid 被执行玩家 UUID
     * @param isPrefix 是否是前缀
     */
    public static void playerPickTitle(@NotNull CommandSource source, String uuid, boolean isPrefix){
        getSqlManager().createReplace(PlayerWear.PLAYER_WEAR.getTableName())
                .setColumnNames("uuid", isPrefix ? "prefix" : "suffix")
                .setParams("uuid", uuid)
                .setParams(isPrefix ? "prefix" : "suffix", null)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );
    }

}
