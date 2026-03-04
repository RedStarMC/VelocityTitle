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
import top.redstarmc.plugin.velocitytitle.velocity.configuration.CommandInfo;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerTitles;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerWear;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.TitleDictionary;
import top.redstarmc.plugin.velocitytitle.velocity.manager.ConfigManager;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;
import top.redstarmc.plugin.velocitytitle.velocity.pojo.CommandResp;
import top.redstarmc.plugin.velocitytitle.velocity.pojo.Title;
import top.redstarmc.plugin.velocitytitle.velocity.pojo.TitleInfoType;
import top.redstarmc.plugin.velocitytitle.velocity.pojo.TitleType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * <b>数据库操作</b><br>
 * 各种数据库操作的方法，全部为静态方法<br>
 * 使用 {@link SQLManager} 进行各种操作<br>
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
    基础查询区
     */

    /**
     * 使用称号名称查询称号信息
     *
     * @param title_name 称号名称
     *
     * @return CompletableFuture<Title> - 异步返回查询结果
     */
    public static @NotNull CompletableFuture<Title> selectTitleWithName(String title_name) {
        if (title_name == null || title_name.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Title> future = new CompletableFuture<>();

        getSqlManager().createQuery()
                .inTable(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .selectColumns("id", "name", "display", "description", "type")
                .addCondition("name", title_name)
                .build().executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if ( result.next() ) {
                                String display = result.getString("display");
                                String description = result.getString("description");
                                int id = result.getInt("id");
                                TitleType type = result.getString("type").equals("prefix") ? TitleType.PREFIX : TitleType.SUFFIX;
                                Title title = new Title(id, title_name, display, description, type);
                                future.complete(title);
                            } else {
                                future.complete(null);
                            }
                        },
                        ((exception, sqlAction) -> future.completeExceptionally(exception))
                );

        return future;
    }


    /**
     * 使用称号 ID 查询称号信息（CompletableFuture）
     *
     * @param title_id 称号ID
     *
     * @return CompletableFuture<Title> - 异步返回查询结果
     */
    public static @NotNull CompletableFuture<Title> selectTitleWithID(int title_id) {
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
                                Title title = new Title(title_id, name, display, description, type.equals("prefix") ? TitleType.PREFIX : TitleType.SUFFIX);
                                future.complete(title);
                            } else {
                                future.complete(null);
                            }
                        },
                        ((exception, sqlAction) -> future.completeExceptionally(exception))
                );

        return future;
    }


    /**
     * 查询玩家是否拥有指定的称号（CompletableFuture）
     *
     * @param player_uuid 玩家UUID
     * @param title_id    称号ID，自增主键，为int类型
     *
     * @return CompletableFuture<CommandResp>
     */
    public static @NotNull CompletableFuture<CommandResp> queryTitleOfPlayer(String player_uuid, int title_id) {
        CompletableFuture<CommandResp> future = new CompletableFuture<>();

        if (player_uuid == null || title_id < 0) {
            return CompletableFuture.completedFuture(CommandResp.PlayerNotOwned);
        }

        getSqlManager().createQuery()
                .inTable(PlayerTitles.tableName)
                .selectColumns("id")
                .addCondition("player_uuid", player_uuid)
                .addCondition("title_id", title_id)
                .build().executeAsync(
                        (query) -> {
                            if ( query.getResultSet().next() ) {
                                future.complete(CommandResp.PlayerOwned);
                            } else {
                                future.complete(CommandResp.PlayerNotOwned);
                            }
                        },
                        (exception, sqlAction) -> future.completeExceptionally(exception)
                );

        return future;
    }

    /**
     * 通过玩家名称获得离线玩家的UUID
     *
     * @param player_name 玩家名称
     *
     * @return 玩家UUID
     */
    public static @NotNull CompletableFuture<String> selectPlayerUUID(String player_name) {
        CompletableFuture<String> future = new CompletableFuture<>();

        getSqlManager().createQuery()
                .inTable(PlayerWear.PLAYER_WEAR.getTableName())
                .selectColumns("player_uuid")
                .addCondition("player_name", player_name)
                .build().executeAsync(
                        (query) -> {
                            try (ResultSet result = query.getResultSet()) {
                                if ( result.next() ) {
                                    future.complete(result.getString("player_uuid"));
                                } else {
                                    future.complete(null);
                                }
                            } catch (SQLException e) {
                                // 捕获 ResultSet 操作抛出的异常，并让 future 异常完成
                                future.completeExceptionally(e);
                            }
                        },
                        (exception, sqlAction) -> future.completeExceptionally(exception)
                );

        return future;
    }


    /*
    非命令调用区
     */

    /**
     * 向数据库保存玩家的UUID以便离线操作
     *
     * @param player_uuid 玩家 UUID
     * @param player_name 玩家名
     */
    public static @NotNull CompletableFuture<Void> savePlayer(String player_uuid, String player_name) {
        return selectPlayerUUID(player_name)
                .thenCompose(uuid -> {
                    if ( uuid != null && ! uuid.isEmpty() ) {
                        return CompletableFuture.completedFuture(null);
                    }

                    CompletableFuture<Void> future = new CompletableFuture<>();
                    getSqlManager().createInsert(PlayerWear.PLAYER_WEAR.getTableName())
                            .setColumnNames("player_uuid", "player_name")
                            .setParams(player_uuid, player_name)
                            .executeAsync(
                                    (query) -> future.complete(null),
                                    ((exception, sqlAction) -> {
                                        logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                                        future.completeExceptionally(exception);
                                    })
                            );
                    return future;
                }).exceptionally(throwable -> {
                    logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return null;
                });
    }

    /**
     * 获得玩家当前穿戴的称号
     *
     * @param player_uuid 玩家的 UUID
     * @param isPrefix    是否为前缀
     *
     * @return 称号实例
     */
    public static @NotNull CompletableFuture<Title> playerWoreTitle(String player_uuid, boolean isPrefix) {
        CompletableFuture<Title> titleCompletableFuture = new CompletableFuture<>();

        getSqlManager().createQuery()
                .inTable(PlayerWear.PLAYER_WEAR.getTableName())
                .selectColumns("player_uuid", isPrefix ? "prefix" : "suffix")
                .addCondition("player_uuid", player_uuid)
                .build()
                .executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if (result.next()) {
                                int id = result.getInt(isPrefix ? "prefix" : "suffix");

                                selectTitleWithID(id)
                                        .thenAccept(titleCompletableFuture :: complete)
                                        .exceptionally(throwable -> {
                                            titleCompletableFuture.completeExceptionally(throwable);
                                            return null;
                                        });

                            } else {
                                titleCompletableFuture.complete(null);
                            }
                        },
                        ((exception, sqlAction) -> titleCompletableFuture.completeExceptionally(exception))
                );

        return titleCompletableFuture.exceptionally(throwable -> {
            logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
            return null;
        });
    }

    /*
     命令调用区
     */

    /**
     * 将一个新称号加入称号库
     *
     * @param title_name  称号名称
     * @param display     实际的展示内容
     * @param description 描述
     * @param isPrefix    是否为前缀
     *
     * @return CompletableFuture<CommandResp> - 异步返回结果
     */
    public static @NotNull CompletableFuture<CommandResp> insertTitle(String title_name, String display, String description, boolean isPrefix) {

        // 检查称号是否存在
        return selectTitleWithName(title_name)
                .thenCompose(existingTitle -> {
                    if (existingTitle != null) {
                        // 称号已存在, 失败
                        return CompletableFuture.completedFuture(CommandResp.TitleExists);
                    }

                    // 插入新称号
                    CompletableFuture<CommandResp> insertFuture = new CompletableFuture<>();
                    try {
                        getSqlManager().createInsert(TitleDictionary.TITLE_DICTIONARY.getTableName())
                                .setColumnNames("name", "display", "description", "type")
                                .setParams(title_name, display, description, isPrefix ? "prefix" : "suffix")
                                .executeAsync(
                                        (query) -> insertFuture.complete(CommandResp.SUCCESS),
                                        ((exception, sqlAction) -> insertFuture.completeExceptionally(exception))
                                );
                    } catch (Exception e) {
                        insertFuture.completeExceptionally(e);
                    }

                    return insertFuture;
                })
                .exceptionally(throwable -> {
                    logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return CommandResp.ERROR;
                });
    }

    /**
     * 更新称号信息
     *
     * @param title_name 称号名称
     * @param type 类型 {@link TitleInfoType}
     * @param data 要更新成的新数据
     */
    public static @NotNull CompletableFuture<CommandResp> updateTitle(String title_name, TitleInfoType type, String data) {
        return selectTitleWithName(title_name)
                .thenCompose(existingTitle -> {
                    if ( existingTitle == null ) {
                        return CompletableFuture.completedFuture(CommandResp.TitleNotFound);
                    }

                    CompletableFuture<CommandResp> insertFuture = new CompletableFuture<>();
                    getSqlManager().createUpdate(TitleDictionary.TITLE_DICTIONARY.getTableName())
                            .addCondition("name", title_name)
                            .addColumnValue(type.getColumnName(), data)
                            .build().executeAsync(
                                    (query) -> insertFuture.complete(CommandResp.SUCCESS),
                                    ((exception, sqlAction) -> insertFuture.completeExceptionally(exception))
                            );

                    return insertFuture;
                })
                .exceptionally(throwable -> {
                    logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return CommandResp.ERROR;
                });
    }


    /**
     * 从称号库删除一个称号
     *
     * @param title_name 称号名称
     */
    public static @NotNull CompletableFuture<CommandResp> deleteTitle(String title_name) {
        return selectTitleWithName(title_name)
                .thenCompose(existingTitle -> {
                    if (existingTitle == null) {
                        // 称号不存在,
                        return CompletableFuture.completedFuture(CommandResp.TitleNotFound);
                    }

                    // 删除称号
                    CompletableFuture<CommandResp> insertFuture = new CompletableFuture<>();
                    try {

                        getSqlManager().createDelete(TitleDictionary.TITLE_DICTIONARY.getTableName())
                                .addCondition("name", title_name)
                                .build()
                                .executeAsync(
                                        (query) -> {
                                            Collection<Player> playerCollection = VelocityTitleVelocity.getInstance().getServer().getAllPlayers();
                                            Player player = playerCollection.stream().skip(ThreadLocalRandom.current().nextInt(playerCollection.size())).findFirst().orElse(null);
                                            if ( player != null ) {
                                                String[] temp = {"DeleteAll"};
                                                VelocityTitleVelocity.getInstance().getPluginMessage().sendMessageT(player, temp);
                                            }
                                            // 没有玩家则不发送
                                            insertFuture.complete(CommandResp.SUCCESS);
                                        },
                                        ((exception, sqlAction) -> insertFuture.completeExceptionally(exception))
                                );

                    } catch (Exception e) {
                        insertFuture.completeExceptionally(e);
                    }

                    return insertFuture;
                })
                .exceptionally(throwable -> {
                    logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return CommandResp.ERROR;
                });

    }

    //-------------------------

    /**
     * 分配称号给玩家
     *
     * @param title_name  称号名称
     * @param player_name 要给的玩家的名字
     */
    public static @NotNull CompletableFuture<CommandResp> divideTitleToPlayer(@NotNull String title_name, @NotNull String player_name) {
        return selectPlayerUUID(player_name)
                .thenCompose(player_uuid -> {
                    if ( player_uuid == null ) {
                        return CompletableFuture.completedFuture(CommandResp.PlayerNotFound);
                    }

                    return selectTitleWithName(title_name)
                            .thenCompose(title -> {
                                if ( title == null ) {
                                    return CompletableFuture.completedFuture(CommandResp.TitleNotFound);
                                }

                                return queryTitleOfPlayer(player_uuid, title.id())
                                        .thenCompose(response -> {
                                            if ( response.equals(CommandResp.PlayerOwned) ) {
                                                return CompletableFuture.completedFuture(response);
                                            }

                                            //授予称号
                                            CompletableFuture<CommandResp> divideFuture = new CompletableFuture<>();
                                            getSqlManager().createInsert(PlayerTitles.tableName)
                                                    .setColumnNames("player_uuid", "title_id", "time_until")
                                                    .setParams(
                                                            player_uuid,
                                                            title.id(),
                                                            0L
                                                    )
                                                    .executeAsync(
                                                            query -> divideFuture.complete(CommandResp.SUCCESS),
                                                            ((exception, sqlAction) -> divideFuture.completeExceptionally(exception))
                                                    );
                                            return divideFuture;
                                        });
                            });
                })
                .exceptionally(exception -> {
                    logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return CommandResp.ERROR;
                });
    }


    /**
     * 收回玩家的称号
     *
     * @param title_name  称号名称
     * @param player_name 被执行玩家的名字
     */
    public static @NotNull CompletableFuture<CommandResp> revokeTitleFromPlayer(@NotNull String title_name, @NotNull String player_name) {
        return selectPlayerUUID(player_name)
                .thenCompose(player_uuid -> {
                    if ( player_uuid == null ) {
                        return CompletableFuture.completedFuture(CommandResp.PlayerNotFound);
                    }

                    return selectTitleWithName(title_name)
                            .thenCompose(title -> {
                                if ( title == null ) {
                                    return CompletableFuture.completedFuture(CommandResp.TitleNotFound);
                                }

                                CompletableFuture<CommandResp> deletePlayerTitle = new CompletableFuture<>();

                                getSqlManager().createDelete(PlayerTitles.PLAYER_TITLES.getTableName())
                                        .addCondition("player_uuid", player_uuid)
                                        .addCondition("title_id", title.id())
                                        .build()
                                        .executeAsync(
                                                (query) -> {
                                                    Optional<Player> optionalPlayer = VelocityTitleVelocity.getInstance()
                                                            .getServer().getPlayer(UUID.fromString(player_uuid));
                                                    if ( optionalPlayer.isPresent() ) {
                                                        Player player = optionalPlayer.get();
                                                        String[] temp = {"DeleteTitle", player_uuid};
                                                        VelocityTitleVelocity.getInstance().getPluginMessage().sendMessageT(player, temp);
                                                    }
                                                    // 不在线则无需发送，当玩家上线时会自动取消。
                                                    deletePlayerTitle.complete(CommandResp.SUCCESS);
                                                },
                                                ((exception, sqlAction) -> deletePlayerTitle.completeExceptionally(exception))
                                        );

                                return deletePlayerTitle;
                            });
                })
                .exceptionally(throwable -> {
                    logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return CommandResp.ERROR;
                });
    }

    //-------------------------

    /**
     * 穿戴称号操作
     *
     * @param title_name  称号名称
     * @param player_name 玩家名
     */
    public static @NotNull CompletableFuture<CommandResp> wearTitle(String title_name, String player_name) {
        return selectPlayerUUID(player_name)
                .thenCompose(player_uuid -> {
                    if ( player_uuid == null ) {
                        CompletableFuture.completedFuture(CommandResp.PlayerNotFound);
                    }

                    return selectTitleWithName(title_name)
                            .thenCompose(title -> {
                                if ( title == null ) {
                                    return CompletableFuture.completedFuture(CommandResp.TitleNotFound);
                                }

                                return queryTitleOfPlayer(player_uuid, title.id())
                                        .thenCompose(response -> {
                                            if ( response.equals(CommandResp.PlayerNotOwned) ) {
                                                return CompletableFuture.completedFuture(response);
                                            }

                                            CompletableFuture<CommandResp> wear = new CompletableFuture<>();

                                            getSqlManager().createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                                                    .addCondition("player_uuid", player_uuid)
                                                    .addColumnValue(title.type().get(), title.id())
                                                    .build().executeAsync(
                                                            (query) -> {
                                                                Optional<Player> optionalPlayer = Optional.empty();
                                                                if ( player_uuid != null ) {
                                                                    optionalPlayer = VelocityTitleVelocity.getInstance()
                                                                            .getServer().getPlayer(UUID.fromString(player_uuid));
                                                                }
                                                                if ( optionalPlayer != null && optionalPlayer.isPresent() ) {
                                                                    Player player = optionalPlayer.get();
                                                                    String[] temp = {"UpdateTitle", player_uuid, title.name(), title.type().get(), title.display()};
                                                                    VelocityTitleVelocity.getInstance().getPluginMessage().sendMessageT(player, temp);
                                                                }
                                                                // 不在线则无需发送，当玩家上线时会自动佩戴。
                                                                wear.complete(CommandResp.SUCCESS);
                                                            },
                                                            ((exception, sqlAction) -> wear.completeExceptionally(exception))
                                                    );

                                            return wear;

                                        });
                            });
                })
                .exceptionally(throwable -> {
                    logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
                    return CommandResp.ERROR;
                });
    }

    /**
     * 摘除前缀或后缀
     * @param player_uuid 被执行玩家 UUID
     * @param type 称号类型
     */
    public static CompletableFuture<CommandResp> playerPickTitle(String player_uuid, TitleType type) {
        CompletableFuture<CommandResp> future = new CompletableFuture<>();

        if ( type == TitleType.ALL ) {
            getSqlManager().createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                    .addCondition("player_uuid", player_uuid)
                    .addColumnValue("prefix", null)
                    .addColumnValue("suffix", null)
                    .build()
                    .executeAsync(
                            (query) -> future.complete(CommandResp.SUCCESS),
                            ((exception, sqlAction) -> future.completeExceptionally(exception))
                    );
        } else if ( type == TitleType.PREFIX ) {
            getSqlManager().createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                    .addCondition("player_uuid", player_uuid)
                    .addColumnValue("prefix", null)
                    .build()
                    .executeAsync(
                            (query) -> future.complete(CommandResp.SUCCESS),
                            ((exception, sqlAction) -> future.completeExceptionally(exception))
                    );
        } else {
            getSqlManager().createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                    .addCondition("player_uuid", player_uuid)
                    .addColumnValue("suffix", null)
                    .build()
                    .executeAsync(
                            (query) -> future.complete(CommandResp.SUCCESS),
                            ((exception, sqlAction) -> future.completeExceptionally(exception))
                    );
        }

        return future.exceptionally(throwable -> {
            logger.crash(throwable, getLanguage().getConfigToml().getString("database.failed-operate"));
            return CommandResp.ERROR;
        });
    }

    /*
    批量查询区
     */

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
                            List<CompletableFuture<Title>> futures = new ArrayList<>();

                            // 收集所有异步查询
                            while (resultSet.next()) {
                                int titleId = resultSet.getInt("title_id");
                                futures.add(selectTitleWithID(titleId));
                            }

                            // 等待所有查询完成后再组装结果
                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                    .thenAccept(v -> {
                                        List<Title> titleList = futures.stream()
                                                .map(CompletableFuture :: join)  // 此时都已完成，不会阻塞
                                                .filter(Objects :: nonNull)
                                                .collect(Collectors.toList());
                                        completableFuture.complete(titleList);
                                    })
                                    .exceptionally(ex -> {
                                        completableFuture.completeExceptionally(ex);
                                        return null;
                                    });
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(CommandInfo.error());
                            completableFuture.completeExceptionally(exception);
                        })
                );

        return completableFuture;
    }

    /**
     * 查询称号列表
     *
     * @param source 命令执行者
     *
     * @return CompletableFuture<List < Title>>
     */
    public static CompletableFuture<List<Title>> selectTitleList(CommandSource source) {
        CompletableFuture<List<Title>> completableFuture = new CompletableFuture<>();

        getSqlManager().createQuery().inTable(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .selectColumns("id", "name", "display", "description", "type")
                .build()
                .executeAsync(
                        (query) -> {
                            ResultSet resultSet = query.getResultSet();
                            List<Title> titleList = new ArrayList<>();

                            while ( resultSet.next() ) {
                                int id = resultSet.getInt("id");
                                String name = resultSet.getString("name");
                                String display = resultSet.getString("display");
                                String description = resultSet.getString("description");
                                String type = resultSet.getString("type");

                                titleList.add(
                                        new Title(id, name, display, description, type.equals("prefix") ? TitleType.PREFIX : TitleType.SUFFIX)
                                );
                            }

                            completableFuture.complete(titleList);
                        },
                        ((exception, sqlAction) -> {
                            logger.crash(exception, getLanguage().getConfigToml().getString("database.failed-operate"));
                            source.sendMessage(CommandInfo.error());
                            completableFuture.completeExceptionally(exception);
                        })
                );

        return completableFuture;
    }


}
