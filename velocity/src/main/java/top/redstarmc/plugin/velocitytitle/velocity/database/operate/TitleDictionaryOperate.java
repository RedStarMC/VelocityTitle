package top.redstarmc.plugin.velocitytitle.velocity.database.operate;

import cc.carm.lib.easysql.api.SQLManager;
import org.jetbrains.annotations.NotNull;
import top.redstarmc.plugin.velocitytitle.velocity.database.Title;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.TitleDictionary;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicReference;

public class TitleDictionaryOperate implements EasySQLOperate {

    /**
     * 将一个新称号加入称号库
     * @param sqlManager 数据库实例
     * @param name 称号的名字
     * @param display 实际的展示内容
     * @param description 描述
     * @param isPrefix 是否为前缀
     */
    public static void insertTitle(@NotNull SQLManager sqlManager, String name, String display, String description, boolean isPrefix){
        sqlManager.createInsert(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .setColumnNames("name", "display", "description", "type")
                .setParams(name, display, description, isPrefix ? "prefix" : "suffix")
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

    /**
     * 查询一个称号信息
     * @param sqlManager 数据库实例
     * @param name 称号的名字
     * @param isPrefix 是否是前缀
     */
    public static Title selectTitle(@NotNull SQLManager sqlManager, String name, boolean isPrefix){
        AtomicReference<Title> title = null;
        sqlManager.createQuery() // 创建一个查询
                .inTable(TitleDictionary.TITLE_DICTIONARY.getTableName()) // 指定表名
                .selectColumns("name", "display", "description", "type") // 选择列
                .addCondition("name", name)
                .addCondition("type", isPrefix ? "prefix" : "suffix")
                .build()/*构建查询体*/.executeAsync(
                        (query) -> {
                            ResultSet result = query.getResultSet();
                            if(result.next()){
                                String display = result.getString("display");
                                String description = result.getString("description");
                                title.set(new Title(name, display, description, isPrefix ? "prefix" : "suffix"));
                            }
                        },
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
        return title.get();
    }

    /**
     * 从称号库删除一个称号
     * @param sqlManager 数据库实例
     * @param name 称号的名字
     */
    public static void deleteTitle(@NotNull SQLManager sqlManager, String name){
        sqlManager.createDelete(TitleDictionary.TITLE_DICTIONARY.getTableName())
                .addCondition("name", name)
                .build()
                .executeAsync((query) -> {},
                        ((exception, sqlAction) -> logger.crash(exception,language.getConfigToml().getString("database.failed-operate")))
                );
    }

}
