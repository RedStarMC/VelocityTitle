package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import cc.carm.lib.easysql.api.SQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerWear;

public class PlayerWearOperate implements EasySQLOperate {

    public static void replaceUUID(SQLManager sqlManager, String uuid, String name) {
        sqlManager.createReplace(PlayerWear.PLAYER_WEAR.getTableName())
                .setColumnNames("uuid", "name")
                .setParams(uuid, name)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }


}
