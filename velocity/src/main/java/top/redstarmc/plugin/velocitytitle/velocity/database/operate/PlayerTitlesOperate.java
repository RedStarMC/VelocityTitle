package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import cc.carm.lib.easysql.api.SQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerTitles;

public class PlayerTitlesOperate implements EasySQLOperate {

    public static void insertPrefix(SQLManager sqlManager, String uuid, String name){
        sqlManager.createInsert(PlayerTitles.PLAYER_TITLES.getTableName())
                .setColumnNames("player_uuid", "title_type" , "title_name")
                .setParams(uuid, "prefix" ,name)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    public static void insertSuffix(SQLManager sqlManager, String uuid, String name){
        sqlManager.createInsert(PlayerTitles.PLAYER_TITLES.getTableName())
                .setColumnNames("player_uuid", "title_type" , "title_name")
                .setParams(uuid, "suffix" ,name)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    public static void deletePrefix(SQLManager sqlManager, String uuid, String name){
        sqlManager.createDelete(PlayerTitles.PLAYER_TITLES.getTableName())
                .addCondition("player_uuid", uuid)
                .addCondition("title_name", name)
                .addCondition("title_type", "prefix")
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    public static void deleteSuffix(SQLManager sqlManager, String uuid, String name){
        sqlManager.createDelete(PlayerTitles.PLAYER_TITLES.getTableName())
                .addCondition("player_uuid", uuid)
                .addCondition("title_name", name)
                .addCondition("title_type", "suffix")
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }
}
