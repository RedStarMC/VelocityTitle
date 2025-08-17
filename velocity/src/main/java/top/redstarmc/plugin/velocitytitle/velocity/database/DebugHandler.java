package top.redstarmc.plugin.velocitytitle.velocity.database;

import cc.carm.lib.easysql.api.SQLAction;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.action.PreparedSQLUpdateAction;
import cc.carm.lib.easysql.api.action.PreparedSQLUpdateBatchAction;
import cc.carm.lib.easysql.api.function.SQLDebugHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.redstarmc.plugin.velocitytitle.velocity.manager.LoggerManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DebugHandler implements SQLDebugHandler {

    private final LoggerManager logger;

    public DebugHandler(LoggerManager logger){
        this.logger = logger;
    }

    /**
     * 该方法将在 {@link SQLAction#execute()} 执行前调用。
     *
     * @param action {@link SQLAction} 对象
     * @param params 执行传入的参数列表。
     *               实际上，仅有 {@link PreparedSQLUpdateAction} 和 {@link PreparedSQLUpdateBatchAction} 才会有传入参数。
     */
    @Override
    public void beforeExecute(@NotNull SQLAction<?> action, @NotNull List<@Nullable Object[]> params) {
        logger.debugDataBase("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.debugDataBase("┣# ActionUUID: {}", action.getActionUUID());
        logger.debugDataBase("┣# ActionType: {}", action.getClass().getSimpleName());
        if (action.getSQLContents().size() == 1) {
            logger.debugDataBase("┣# SQLContent: {}", action.getSQLContents().get(0));
        } else {
            logger.debugDataBase("┣# SQLContents: ");
            int i = 0;
            for (String sqlContent : action.getSQLContents()) {
                logger.debugDataBase("┃ - [{}] {}", ++i, sqlContent);
            }
        }
        if (params.size() == 1) {
            Object[] param = params.get(0);
            if (param != null) {
                logger.debugDataBase("┣# SQLParam: {}", parseParams(param));
            }
        } else if (params.size() > 1) {
            logger.debugDataBase("┣# SQLParams: ");
            int i = 0;
            for (Object[] param : params) {
                logger.debugDataBase("┃ - [{}] {}", ++i, parseParams(param));
            }
        }
        logger.debugDataBase("┣# CreateTime: {}", action.getCreateTime(TimeUnit.MILLISECONDS));
        logger.debugDataBase("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * 该方法将在 {@link SQLQuery#close()} 执行后调用。
     *
     * @param query           {@link SQLQuery} 对象
     * @param executeNanoTime 该次查询开始执行的时间 (单位：纳秒)
     * @param closeNanoTime   该次查询彻底关闭的时间 (单位：纳秒)
     */
    @Override
    public void afterQuery(@NotNull SQLQuery query, long executeNanoTime, long closeNanoTime) {
        logger.debugDataBase("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.debugDataBase("┣# ActionUUID: {}", query.getAction().getActionUUID());
        logger.debugDataBase("┣# SQLContent: {}", query.getSQLContent());
        logger.debugDataBase("┣# CloseTime: {}  (cost {} ms)",
                TimeUnit.NANOSECONDS.toMillis(closeNanoTime),
                ((double) (closeNanoTime - executeNanoTime) / 1000000)
        );
        logger.debugDataBase("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

}
