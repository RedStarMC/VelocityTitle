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
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerTitles;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.TitleDictionary;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;
import top.redstarmc.plugin.velocitytitle.velocity.record.Title;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static net.kyori.adventure.text.Component.text;

/**
 * <h1>数据库操作</h1>
 * 各种数据库操作的实现。<br>
 * 使用 {@link SQLManager} 进行各种操作<br>
 * TODO 问题：是否需要异步处理？如果需要，怎么进行异步处理？
 * TODO 错误信息和成功信息反馈
 */
public class DataBaseOperate {

    static LoggerManager logger = VelocityTitleVelocity.getInstance().getLogger();

    static ExecutorService DB_POOL = VelocityTitleVelocity.getDbPool();

    private static ConfigManager getLanguage() {
        return VelocityTitleVelocity.getInstance().getLanguage();
    }

    private static SQLManager getSqlManager() {
        return VelocityTitleVelocity.getInstance().getDBManager().getSqlManager();
    }

    /*
    查询
     */

    /**
     * 查询称号信息（CompletableFuture）
     * @param source 命令发送者
     * @param name 称号名称
     * @return CompletableFuture<Title> - 异步返回查询结果
     */
    public static @NotNull CompletableFuture<Title> selectTitle(CommandSource source, String name) {
        CompletableFuture<Title> future = new CompletableFuture<>();

        if (name == null || name.isEmpty()) {
            future.complete(null);
            return future;
        }

        try {
            getSqlManager().createQuery()
                    .inTable(TitleDictionary.TITLE_DICTIONARY.getTableName())
                    .selectColumns("id", "name", "display", "description", "type")
                    .addCondition("name", name)
                    .build().executeAsync(
                    (query) -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                String display = result.getString("display");
                                String description = result.getString("description");
                                int id = result.getInt("id");
                                boolean isPrefix = result.getString("type").equals("suffix");
                                Title title = new Title(id, name, display, description, isPrefix);
                                future.complete(title);
                            } else {
                                future.complete(null);
                            }
                    },
                    ((exception, sqlAction) -> {
                        logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                        source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        future.completeExceptionally(exception);
                    })
            );
        } catch (Exception exception) {
            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
            future.completeExceptionally(exception);
        }

        return future;
    }

    /**
     * 查询玩家是否拥有指定的称号（CompletableFuture）
     * @param UUID 玩家UUID
     * @param id 称号ID，自增主键，为int类型
     * @return CompletableFuture<Boolean>
     */
    public static CompletableFuture<Boolean> queryTitleOfPlayer(CommandSource source, String UUID, int id) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (UUID == null || id < 0) {
            future.complete(false);
            return future;
        }

        try {
            getSqlManager().createQuery()
                    .inTable(PlayerTitles.tableName)
                    .selectColumns("id")
                    .addCondition("player_uuid", UUID)
                    .addCondition("title_id", id)
                    .build().executeAsync(
                    (query) -> future.complete(query.getResultSet().next()),
                    (exception, sqlAction) -> {
                        logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                        source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        future.completeExceptionally(exception);
                    }
            );
        } catch (Exception exception) {
            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
            future.completeExceptionally(exception);
        }

        return future;
    }


    /*
     插入
     */

    /**
     * 将一个新称号加入称号库
     * @param source 命令发送者
     * @param name 称号名称
     * @param display 实际的展示内容
     * @param description 描述
     * @param isPrefix 是否为前缀
     * @return CompletableFuture<Boolean> - 异步返回是否成功
     */
    public static CompletableFuture<Boolean> insertTitle(@NotNull CommandSource source, String name, String display, String description, boolean isPrefix) {

        // 检查称号是否存在
        return selectTitle(source, name)
                .thenCompose(existingTitle -> {
                    if (existingTitle != null) {
                        // 称号已存在, 失败
                        return CompletableFuture.completedFuture(false);
                    }

                    // 插入新称号
                    CompletableFuture<Boolean> insertFuture = new CompletableFuture<>();
                    try {
                        getSqlManager().createInsert(TitleDictionary.TITLE_DICTIONARY.getTableName())
                                .setColumnNames("name", "display", "description", "type")
                                .setParams(name, display, description, isPrefix ? "prefix" : "suffix")
                                .executeAsync(
                                        (query) -> insertFuture.complete(true),
                                        ((exception, sqlAction) -> {
                                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                                            insertFuture.completeExceptionally(exception);
                                        })
                                );

                    } catch (Exception e) {
                        logger.crash(e, getLanguage().getConfigToml().getString("database.failed-operate"));
                        source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        insertFuture.completeExceptionally(e);
                    }

                    return insertFuture;
                })
                .exceptionally(e -> {
                    logger.crash(e, getLanguage().getConfigToml().getString("database.failed-operate"));
                    source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                    return false;
                });
    }


    /**
     * 编辑一个称号的信息
     * @param source 命令发送者
     * @param title {@link Title} 要编辑的称号实例
     */
    public static CompletableFuture<Boolean> updateTitle(@NotNull CommandSource source, @NotNull Title title){
        return selectTitle(source, title.name())
                .thenCompose(existingTitle -> {
                    if (existingTitle == null) {
                        // 称号不存在, 失败
                        return CompletableFuture.completedFuture(false);
                    }

                    // 更新称号
                    CompletableFuture<Boolean> insertFuture = new CompletableFuture<>();
                    try {

                        getSqlManager().createUpdate(TitleDictionary.TITLE_DICTIONARY.getTableName())
                                .addCondition("name", title.name())
                                .setColumnValues("display", title.display())
                                .setColumnValues("description", title.description())
                                .build().executeAsync(
                                        (query) -> insertFuture.complete(true),
                                        ((exception, sqlAction) -> {
                                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                                            insertFuture.completeExceptionally(exception);
                                        })
                                );

                    } catch (Exception e) {
                        logger.crash(e, getLanguage().getConfigToml().getString("database.failed-operate"));
                        source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        insertFuture.completeExceptionally(e);
                    }

                    return insertFuture;
                })
                .exceptionally(e -> {
                    logger.crash(e, getLanguage().getConfigToml().getString("database.failed-operate"));
                    source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                    return false;
                });
    }

    /**
     * 从称号库删除一个称号
     * @param source 命令发送者
     * @param name 称号名称
     */
    public static CompletableFuture<Boolean> deleteTitle(@NotNull CommandSource source, String name){

        return selectTitle(source, name)
                .thenCompose(existingTitle -> {
                    if (existingTitle == null) {
                        // 称号不存在, 失败
                        return CompletableFuture.completedFuture(false);
                    }

                    // 更新称号
                    CompletableFuture<Boolean> insertFuture = new CompletableFuture<>();
                    try {

                        getSqlManager().createDelete(TitleDictionary.TITLE_DICTIONARY.getTableName())
                                .addCondition("name", name)
                                .build()
                                .executeAsync((query) -> insertFuture.complete(true),
                                        ((exception, sqlAction) -> {
                                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                                            insertFuture.completeExceptionally(exception);
                                        })
                                );

                    } catch (Exception e) {
                        logger.crash(e, getLanguage().getConfigToml().getString("database.failed-operate"));
                        source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        insertFuture.completeExceptionally(e);
                    }

                    return insertFuture;
                })
                .exceptionally(e -> {
                    logger.crash(e, getLanguage().getConfigToml().getString("database.failed-operate"));
                    source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                    return false;
                });

    }

    //-------------------------


    /**
     * 分配称号给玩家
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param player_uuid 要给的玩家的 uuid
     */
    public static CompletableFuture<Boolean> divideTitleToPlayer(@NotNull CommandSource source,@NotNull String name,@NotNull String player_uuid){

        return selectTitle(source, name)
                .thenCompose(title -> {
                    if (title == null){
                        return CompletableFuture.completedFuture(false);
                    }

                    return queryTitleOfPlayer(source, player_uuid, title.id())
                            .thenCompose(isExist-> {
                                if (isExist){
                                    source.sendMessage(text("玩家已经拥有该称号"));
                                    return CompletableFuture.completedFuture(false);
                                }

                                //授予称号
                                CompletableFuture<Boolean> divideFuture = new CompletableFuture<>();
                                getSqlManager().createInsert(PlayerTitles.tableName)
                                        .setColumnNames("player_uuid", "title_name", "time_until")
                                        .setParams(
                                                player_uuid,
                                                title.id(),
                                                0
                                        )
                                        .executeAsync(
                                                query -> divideFuture.complete(true),
                                                ((exception, sqlAction) -> {
                                                    logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                                    source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                                                    divideFuture.completeExceptionally(exception);
                                                })
                                        );
                                return divideFuture;
                            });
                })
                .exceptionally(exception -> {
                    logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                    source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                    return false;
                });
    }


    /**
     * 收回玩家的称号
     * @param source 命令发送者
     * @param name 称号识别 ID
     * @param UUID 被执行玩家 UUID
     */
    public static @NotNull CompletableFuture<Boolean> retrieveTitleFromPlayer(@NotNull CommandSource source,@NotNull String name,@NotNull String UUID){
        CompletableFuture<Boolean> deletePlayerTitle = new CompletableFuture<>();

        getSqlManager().createDelete(PlayerTitles.tableName)
                .addCondition("title_name", name)
                .addCondition("player_uuid", UUID)
                .build()
                .executeAsync((query) -> deletePlayerTitle.complete(true),
                        ((exception, sqlAction) -> {
                            deletePlayerTitle.completeExceptionally(exception);
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                        })
                );

        return deletePlayerTitle;
    }

    //-------------------------

//    /**
//     * 穿戴一个称号
//     * @param source 命令发送者
//     * @param name 称号识别 ID
//     * @param uuid 被执行玩家 UUID
//     */
//    public static void playerWearTitle(@NotNull CommandSource source, String name, String uuid) {
//        //1.判断称号是否可用
//        if (!queryTitleOfPlayer(source, name, uuid)) return;
//        //2.查询是前缀还是后缀
//        Title title = selectTitle(source, name);
//        //3.加入
//        getSqlManager().createReplace(PlayerWear.PLAYER_WEAR.getTableName())
//                .setColumnNames("player_uuid", title.isPrefix() ? "prefix" : "suffix")
//                .setParams(title.isPrefix() ? "prefix" : "suffix", name)
//                .executeAsync((query) -> {},
//                        ((exception, sqlAction) -> {
//                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
//                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
//                        })
//                );
//    }
//
//    /**
//     * 查询玩家当前穿戴的称号
//     * @param source 命令发送者
//     * @param uuid 被执行玩家 UUID
//     * @param isPrefix 是否是前缀
//     */
//    public static Title playerWoreTitle(@NotNull CommandSource source, String uuid, boolean isPrefix){
//        AtomicReference<String> name = null;
//        getSqlManager().createQuery()
//                .inTable(PlayerWear.PLAYER_WEAR.getTableName())
//                .selectColumns("player_uuid", isPrefix ? "prefix" : "suffix")
//                .addCondition("player_uuid", uuid)
//                .build()
//                .executeAsync((query) -> {
//                            ResultSet result = query.getResultSet();
//                            if(result.next()){
//                                name.set(result.getString(isPrefix ? "prefix" : "suffix"));
//                            }
//                        },
//                        ((exception, sqlAction) -> {
//                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
//                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
//                        })
//                );
//        if(name.get() == null) return null;
////        return selectTitle(source, name.get());
//        return null;
//    }
//
//    /**
//     * 摘除前缀或后缀
//     * @param source 命令发送者
//     * @param uuid 被执行玩家 UUID
//     * @param isPrefix 是否是前缀
//     */
//    public static void playerPickTitle(@NotNull CommandSource source, String uuid, boolean isPrefix){
//        getSqlManager().createReplace(PlayerWear.PLAYER_WEAR.getTableName())
//                .setColumnNames("uuid", isPrefix ? "prefix" : "suffix")
//                .setParams("uuid", uuid)
//                .setParams(isPrefix ? "prefix" : "suffix", null)
//                .executeAsync((query) -> {},
//                        ((exception, sqlAction) -> {
//                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
//                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
//                        })
//                );
//    }

}
