package top.redstarmc.plugin.velocitytitle.velocity.manager;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cc.carm.lib.easysql.hikari.HikariDataSource;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import top.redstarmc.plugin.velocitytitle.velocity.database.DebugHandler;

import java.sql.SQLException;

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
     * 从 {@link top.redstarmc.plugin.velocitytitle.velocity.VelocityTitleVelocity } 调用。数据库入口方法。
     */
    public void init(){
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

        sqlManager.setDebugHandler(new DebugHandler(logger));
        sqlManager.setDebugMode(config.getConfigToml().getBoolean("debug-mode"));

        try {
            if (!sqlManager.getConnection().isValid(5)) {
                logger.error(language.getConfigToml().getString("database.timeout"));
            }

            // 注册数据表

        } catch (SQLException e) {
            logger.error(language.getConfigToml().getString("database.failed"));
            logger.debug(e.getMessage(),e);
        }
    }

    /**
     * 初始化嵌入式数据库的 {@link cc.carm.lib.easysql.api.SQLManager}
     */
    private void initEmbedded(){
        String diver = config.getConfigToml().getString("database.driver");
        String url = config.getConfigToml().getString("database.url");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(diver);
        config.setJdbcUrl(url);

        sqlManager = new SQLManagerImpl(new HikariDataSource(config), "test");

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


    public static SQLManagerImpl getSqlManager() {
        return sqlManager;
    }
}
