package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import cc.carm.lib.easysql.api.SQLManager;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.SuffixDictionary;

public class SuffixDictionaryOperate implements EasySQLOperate {

    public static void insertTitle(SQLManager sqlManager, String name, String display, String description){
        sqlManager.createInsert(SuffixDictionary.SUFFIX_DICTIONARY.getTableName())
                .setColumnNames("name", "display", "description")
                .setParams(name, display, description)
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }
}
