package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import cc.carm.lib.easysql.api.SQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PrefixDictionary;

public class PrefixDictionaryOperate implements EasySQLOperate {

    public static void insertTitle(SQLManager sqlManager, String name, String display, String description){
        sqlManager.createInsert(PrefixDictionary.PREFIX_DICTIONARY.getTableName())
                .setColumnNames("name", "display", "description")
                .setParams(name, display, description)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    public static void deleteTitle(SQLManager sqlManager, String name){
        sqlManager.createDelete(PrefixDictionary.PREFIX_DICTIONARY.getTableName())
                .addCondition("name", name)
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

}
