package top.redstarmc.plugin.velocitytitle.velocity.manager;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cc.carm.lib.easysql.hikari.HikariDataSource;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity;
import top.redstarmc.plugin.velocitytitle.velocity.database.DebugHandler;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerTitles;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.PlayerWear;
import top.redstarmc.plugin.velocitytitle.velocity.database.table.TitleDictionary;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>数据库管理器</h1>
 * 使用 {@link cc.carm.lib.easysql.EasySQL} 数据库操作库。
 */
public class EasySQLManager {

    private static SQLManagerImpl sqlManager;

    private final LoggerManager logger;

    private final ConfigManager config;

    private final ConfigManager language;

    public EasySQLManager(LoggerManager logger, ConfigManager config, ConfigManager language) {
        this.logger = logger;
        this.config = config;
        this.language = language;
    }

    /**
     * <h2>初始化数据库</h2>
     * 从 {@link VelocityTitleVelocity } 调用。数据库入口方法。
     */
    public void init(){

        //数据库模式配置
        String mode = config.getConfigToml().getString("database.mode");
        if(mode.equals("Embedded")){
            logger.info(language.getConfigToml().getString("database.embedded"));
            initEmbedded();
        } else if (mode.equals("Server")) {
            logger.info(language.getConfigToml().getString("database.server"));
            initServer();
        }else {
            logger.info(language.getConfigToml().getString("database.other"));
            initEmbedded();
        }

        //错误处理器配置
        sqlManager.setDebugHandler(new DebugHandler(logger));
        sqlManager.setDebugMode(config.getConfigToml().getBoolean("debug-mode"));

        //线程池配置
        ExecutorService pool = Executors.newFixedThreadPool(Math.toIntExact(config.getConfigToml().getLong("database.executor-pool")));
        sqlManager.setExecutorPool(pool);

        try {
            if (!sqlManager.getConnection().isValid(5)) {
                logger.error(language.getConfigToml().getString("database.timeout")); //超时
            }

            // 注册数据表
            logger.debugDataBase("正在尝试注册数据表");

            TitleDictionary.initialize(sqlManager);
            PlayerTitles.initialize(sqlManager);
            PlayerWear.initialize(sqlManager);

            logger.debugDataBase("数据表注册执行完毕");

        } catch (SQLException e) {
            logger.error(language.getConfigToml().getString("database.failed"));
            logger.debug(e.getMessage(),e);
        }
    }

    /**
     * 初始化嵌入式数据库的 {@link cc.carm.lib.easysql.api.SQLManager}
     */
    private void initEmbedded(){
        String driver = config.getConfigToml().getString("database.driver");
        String url = config.getConfigToml().getString("database.url");
        String username = config.getConfigToml().getString("database.username");
        String password = config.getConfigToml().getString("database.password");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(url);
        // 新增：设置用户名和密码
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        sqlManager = new SQLManagerImpl(new HikariDataSource(hikariConfig), "test");

    }

    /**
     * 初始化服务器（远程链接）数据库的 {@link cc.carm.lib.easysql.api.SQLManager}
     */
    private void initServer(){
        String diver = config.getConfigToml().getString("database.driver");
        String url = config.getConfigToml().getString("database.url");
        String username = config.getConfigToml().getString("database.username");
        String password = config.getConfigToml().getString("database.password");

        sqlManager = EasySQL.createManager(diver,url,username,password);
    }


    public SQLManagerImpl getSqlManager() {
        return sqlManager;
    }
}
