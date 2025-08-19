package org.example.flashytitles.core.database;

/**
 * 数据库配置类
 */
public class DatabaseConfig {
    private final String type;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String sqliteFile;
    
    public DatabaseConfig(String type, String host, int port, String database, 
                         String username, String password, String sqliteFile) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sqliteFile = sqliteFile;
    }
    
    // Getters
    public String getType() { return type; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSqliteFile() { return sqliteFile; }
}
