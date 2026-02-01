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
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerTitles;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerWear;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.TitleDictionary;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

/**
 * <h1>数据库操作</h1>
 * 各种数据库操作的方法，全部为静态方法<br>
 * 使用 {@link SQLManager} 进行各种操作<br>
 * TODO 错误信息和成功信息反馈
 */
public class DataBaseOperate {

    static LoggerManager logger = VelocityTitleVelocity.getInstance().getLogger();

    private static ConfigManager getLanguage() {
        return VelocityTitleVelocity.getInstance().getLanguage();
        //
    }

    private static SQLManager getSqlManager() {
        return VelocityTitleVelocity.getInstance().getDBManager().getSqlManager();
        //
    }

    /*
    查询
     */

    /**
     * 使用称号名称查询称号信息（CompletableFuture）
     *
     * @param source     命令发送者
     * @param title_name 称号名称
     *
     * @return CompletableFuture<Title> - 异步返回查询结果
     */
    public static @NotNull CompletableFuture<Title> selectTitleWithName(CommandSource source, String title_name) {
        CompletableFuture<Title> future = new CompletableFuture<>();

        if (title_name == null || title_name.isEmpty()) {
            future.complete(null);
            return future;
        }

        try {
            getSqlManager().createQuery()
                    .inTable(TitleDictionary.TITLE_DICTIONARY.getTableName())
                    .selectColumns("id", "name", "display", "description", "type")
                    .addCondition("name", title_name)
                    .build().executeAsync(
                            (query) -> {
                                ResultSet result = query.getResultSet();
                                if (result.next()) {
                                    String display = result.getString("display");
                                    String description = result.getString("description");
                                    int id = result.getInt("id");
                                    boolean isPrefix = result.getString("type").equals("prefix");
                                    Title title = new Title(id, title_name, display, description, isPrefix);
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
     * 使用称号 ID 查询称号信息（CompletableFuture）
     *
     * @param source   命令发送者
     * @param title_id 称号ID
     *
     * @return CompletableFuture<Title> - 异步返回查询结果
     */
    public static @NotNull CompletableFuture<Title> selectTitleWithID(CommandSource source, int title_id) {
        CompletableFuture<Title> future = new CompletableFuture<>();

        getSqlManager().createQuery()
                .inTable(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .selectColumns("id", "name", "display", "description", "type")
                .addCondition("id", title_id)
                .build().executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                String display = result.getString("display");
                                String description = result.getString("description");
                                String name = result.getString("name");
                                String type = result.getString("type");
                                Title title = new Title(title_id, name, display, description, type.equals("prefix"));
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

        return future;
    }


    /**
     * 查询玩家是否拥有指定的称号（CompletableFuture）
     *
     * @param player_uuid 玩家UUID
     * @param title_id    称号ID，自增主键，为int类型
     *
     * @return CompletableFuture<Boolean>
     */
    public static CompletableFuture<Boolean> queryTitleOfPlayer(CommandSource source, String player_uuid, int title_id) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (player_uuid == null || title_id < 0) {
            future.complete(false);
            return future;
        }

        try {
            getSqlManager().createQuery()
                    .inTable(PlayerTitles.tableName)
                    .selectColumns("id")
                    .addCondition("player_uuid", player_uuid)
                    .addCondition("title_id", title_id)
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

    /**
     * 通过玩家名称获得离线玩家的UUID
     *
     * @param source      命令执行者
     * @param player_name 玩家名称
     *
     * @return 玩家UUID
     */
    public static CompletableFuture<String> selectPlayerUUID(CommandSource source, String player_name) {
        CompletableFuture<String> uuidCompletableFuture = new CompletableFuture<>();

        getSqlManager().createQuery()
                .inTable(PlayerWear.PLAYER_WEAR.getTableName())
                .selectColumns("player_uuid")
                .addCondition("player_name", player_name)
                .build().executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                uuidCompletableFuture.complete(result.getString("player_uuid"));
                            }
                        },
                        (exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                            uuidCompletableFuture.completeExceptionally(exception);
                        }
                );

        return uuidCompletableFuture;
    }


    /*
     插入
     */

    /**
     * 将一个新称号加入称号库
     *
     * @param source      命令发送者
     * @param title_name  称号名称
     * @param display     实际的展示内容
     * @param description 描述
     * @param isPrefix    是否为前缀
     *
     * @return CompletableFuture<Boolean> - 异步返回是否成功
     */
    public static CompletableFuture<Boolean> insertTitle(@NotNull CommandSource source, String title_name, String display, String description, boolean isPrefix) {

        // 检查称号是否存在
        return selectTitleWithName(source, title_name)
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
                                .setParams(title_name, display, description, isPrefix ? "prefix" : "suffix")
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
        return selectTitleWithName(source, title.name())
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
     *
     * @param source     命令发送者
     * @param title_name 称号名称
     */
    public static CompletableFuture<Boolean> deleteTitle(@NotNull CommandSource source, String title_name) {

        return selectTitleWithName(source, title_name)
                .thenCompose(existingTitle -> {
                    if (existingTitle == null) {
                        // 称号不存在, 失败
                        return CompletableFuture.completedFuture(false);
                    }

                    // 删除称号
                    CompletableFuture<Boolean> insertFuture = new CompletableFuture<>();
                    try {

                        getSqlManager().createDelete(TitleDictionary.TITLE_DICTIONARY.getTableName())
                                .addCondition("name", title_name)
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
     *
     * @param source      命令发送者
     * @param title_name  称号名称
     * @param player_uuid 要给的玩家的 uuid
     */
    public static CompletableFuture<Boolean> divideTitleToPlayer(@NotNull CommandSource source, @NotNull String title_name, @NotNull String player_uuid) {

        return selectTitleWithName(source, title_name)
                .thenCompose(title -> {
                    if (title == null) {
                        return CompletableFuture.completedFuture(false);
                    }

                    return queryTitleOfPlayer(source, player_uuid, title.id())
                            .thenCompose(isExist -> {
                                if (isExist) {
                                    source.sendMessage(text("玩家已经拥有该称号"));
                                    return CompletableFuture.completedFuture(false);
                                }

                                //授予称号
                                CompletableFuture<Boolean> divideFuture = new CompletableFuture<>();
                                getSqlManager().createInsert(PlayerTitles.tableName)
                                        .setColumnNames("player_uuid", "title_id", "time_until")
                                        .setParams(
                                                player_uuid,
                                                title.id(),
                                                0L
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
     *
     * @param source      命令发送者
     * @param title_name  称号识别 ID
     * @param player_uuid 被执行玩家 UUID
     */
    public static @NotNull CompletableFuture<Boolean> retrieveTitleFromPlayer(
            @NotNull CommandSource source, @NotNull String title_name, @NotNull String player_uuid) {

        return selectTitleWithName(source, title_name)
                .thenCompose(title -> {
                    CompletableFuture<Boolean> deletePlayerTitle = new CompletableFuture<>();

                    getSqlManager().createDelete(PlayerTitles.PLAYER_TITLES.getTableName())
                            .addCondition("player_uuid", player_uuid)
                            .addCondition("title_id", title.id())
                            .build()
                            .executeAsync(
                                    (query) -> deletePlayerTitle.complete(true),
                                    ((exception, sqlAction) -> {
                                        logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                        source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                                        deletePlayerTitle.completeExceptionally(exception);
                                    })
                            );

                    return deletePlayerTitle;
                });
    }

    //-------------------------

    public static CompletableFuture<Boolean> savePlayer(String player_uuid, String player_name){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        getSqlManager().createReplace(PlayerWear.PLAYER_WEAR.getTableName())
                .setColumnNames("player_uuid", "player_name")
                .setParams(player_uuid, player_name)
                .executeAsync(
                        (query) -> completableFuture.complete(true),
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            completableFuture.completeExceptionally(exception);
                        })

                );


        return completableFuture;
    }

    /**
     * 穿戴称号操作
     *
     * @param source      命令执行者
     * @param title_name  称号名称
     * @param player_uuid 玩家 UUID
     *
     * @return 是否成功执行
     */
    public static CompletableFuture<Boolean> wearTitle(@NotNull CommandSource source, String title_name, String player_uuid) {
        return selectTitleWithName(source, title_name)
                .thenCompose(title -> {
                    if (title == null) {
                        return CompletableFuture.completedFuture(false);
                    }

                    return queryTitleOfPlayer(source, player_uuid, title.id())
                            .thenCompose(isExist -> {
                                if (! isExist) {
                                    return CompletableFuture.completedFuture(false);
                                }

                                CompletableFuture<Boolean> wear = new CompletableFuture<>();

                                getSqlManager().createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                                        .addCondition("player_uuid", player_uuid)
                                        .setColumnValues(title.isPrefix() ? "prefix" : "suffix", title.id())
                                        .build().executeAsync(
                                                (query) -> {
                                                    Optional<Player> optionalPlayer = VelocityTitleVelocity.getInstance().getServer().getPlayer(player_uuid);
                                                    if (optionalPlayer.isPresent()) {
                                                        Player player = optionalPlayer.get();
                                                        String[] temp = {"UpdateTitle", player_uuid, title.name(), title.isPrefix() ? "prefix" : "suffix", title.display()};
                                                        VelocityTitleVelocity.getInstance().getPluginMessage().sendMessageT(player/*随机挑选玩家*/, temp);
                                                    }
                                                    // 不在线则无需发送，当玩家上线时会自动佩戴。
                                                    wear.complete(true);
                                                },
                                                ((exception, sqlAction) -> {
                                                    logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                                    source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                                                    wear.completeExceptionally(exception);
                                                })
                                        );

                                return wear;

                            });
                });
    }

    public static CompletableFuture<Title> playerWoreTitle(@NotNull CommandSource source, String uuid, boolean isPrefix){
        CompletableFuture<Title> titleCompletableFuture = new CompletableFuture<>();

        getSqlManager().createQuery()
                .inTable(PlayerWear.PLAYER_WEAR.getTableName())
                .selectColumns("player_uuid", isPrefix ? "prefix" : "suffix")
                .addCondition("player_uuid", uuid)
                .build()
                .executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                int id = result.getInt(isPrefix ? "prefix" : "suffix");

                                selectTitleWithID(source, id).thenAcceptAsync(titleCompletableFuture::complete);

                            } else {
                                logger.error("特殊触发 top.redstarmc.plugin.velocitytitle.velocity.database.DataBaseOperate.playerWoreTitle");
                                titleCompletableFuture.complete(null);
                            }
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                            titleCompletableFuture.completeExceptionally(exception);
                        })
                );


        return  titleCompletableFuture;
    }

    /**
     * 摘除前缀或后缀
     * @param source 命令发送者
     * @param uuid 被执行玩家 UUID
     * @param isPrefix 是否是前缀
     */
    public static CompletableFuture<Boolean> playerPickTitle(@NotNull CommandSource source, String uuid, boolean isPrefix){
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        getSqlManager().createReplace(PlayerWear.PLAYER_WEAR.getTableName())
                .setColumnNames("uuid", isPrefix ? "prefix" : "suffix")
                .setParams("uuid", uuid)
                .setParams(isPrefix ? "prefix" : "suffix", null)
                .executeAsync(
                        (query) -> future.complete(true),
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                            future.completeExceptionally(exception);
                        })
                );

        return future;
    }


    /**
     * 查询玩家拥有的称号的列表
     *
     * @param source      命令执行者
     * @param player_uuid 玩家uuid
     *
     * @return CompletableFuture<List < Title>>
     */
    public static CompletableFuture<List<Title>> selectPlayerTitleList(CommandSource source, String player_uuid) {
        CompletableFuture<List<Title>> completableFuture = new CompletableFuture<>();

        getSqlManager().createQuery().inTable(PlayerTitles.PLAYER_TITLES.getTableName())
                .selectColumns("player_uuid", "title_id")
                .addCondition("player_uuid", player_uuid)
                .build()
                .executeAsync(
                        (query) -> {
                            ResultSet resultSet = query.getResultSet();
                            List<Title> titleList = new ArrayList<>();

                            while (resultSet.next()) {
                                int titleId = resultSet.getInt("title_id");
                                selectTitleWithID(source, titleId)
                                        .thenAcceptAsync(title -> {
                                            if (title != null) {
                                                titleList.add(title);
                                            }
                                        });
                            }

                            completableFuture.complete(titleList);
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(text(getLanguage().getConfigToml().getString("commands.error")));
                            completableFuture.completeExceptionally(exception);
                        })
                );

        return completableFuture;
    }


}
