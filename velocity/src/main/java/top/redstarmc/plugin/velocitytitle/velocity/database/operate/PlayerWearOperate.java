package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import cc.carm.lib.easysql.api.SQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerWear;

@Deprecated
public class PlayerWearOperate implements EasySQLOperate {

    public static void replaceUUID(SQLManager sqlManager, String uuid, String name) {
        sqlManager.createReplace(PlayerWear.PLAYER_WEAR.getTableName())
                .setColumnNames("uuid", "name")
                .setParams(uuid, name)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    public static void updatePrefix(SQLManager sqlManager, String uuid, String prefix){
        sqlManager.createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                .addColumnValue("uuid", uuid)
                .addColumnValue("prefix", prefix)
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    public static void updateSuffix(SQLManager sqlManager, String uuid, String suffix){
        sqlManager.createUpdate(PlayerWear.PLAYER_WEAR.getTableName())
                .addColumnValue("uuid", uuid)
                .addColumnValue("suffix", suffix)
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }
}
