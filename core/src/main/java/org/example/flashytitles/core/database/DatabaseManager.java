package org.example.flashytitles.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.flashytitles.core.model.Title;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库管理器
 * 支持 MySQL 和 SQLite
 */
public class DatabaseManager {
    
    private final DatabaseConfig config;
    private HikariDataSource dataSource;
    
    // 缓存
    private final Map<String, Title> titleCache = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> ownedCache = new ConcurrentHashMap<>();
    private final Map<UUID, String> equippedCache = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> coinsCache = new ConcurrentHashMap<>();
    
    public DatabaseManager(DatabaseConfig config) {
        this.config = config;
    }
    
    /**
     * 初始化数据库连接
     */
    public void initialize() throws SQLException {
        int maxRetries = 3;
        int retryDelay = 5000; // 5秒

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                initializeConnection();

                // 创建表
                createTables();

                // 加载缓存
                loadCache();

                System.out.println("数据库初始化成功 (尝试 " + attempt + "/" + maxRetries + ")");
                return;

            } catch (SQLException e) {
                System.err.println("数据库初始化失败 (尝试 " + attempt + "/" + maxRetries + "): " + e.getMessage());

                if (attempt == maxRetries) {
                    throw new SQLException("数据库初始化失败，已尝试 " + maxRetries + " 次", e);
                }

                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("数据库初始化被中断", ie);
                }
            }
        }
    }

    /**
     * 初始化数据库连接配置
     */
    private void initializeConnection() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        if (config.getType().equalsIgnoreCase("mysql")) {
            hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&characterEncoding=utf8",
                    config.getHost(), config.getPort(), config.getDatabase()));
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else {
            // 默认使用H2数据库
            String dbPath = config.getSqliteFile();
            if (dbPath == null || dbPath.isEmpty()) {
                dbPath = "./data/flashytitles";
            }
            hikariConfig.setJdbcUrl("jdbc:h2:file:" + dbPath + ";DB_CLOSE_DELAY=-1;MODE=MYSQL");
            hikariConfig.setDriverClassName("org.h2.Driver");
            hikariConfig.setUsername("sa");
            hikariConfig.setPassword("");
        }

        // 连接池配置
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setLeakDetectionThreshold(60000);

        // 连接验证
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setValidationTimeout(3000);

        dataSource = new HikariDataSource(hikariConfig);

        // 测试连接
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(5)) {
                throw new SQLException("数据库连接验证失败");
            }
        }
    }
    
    /**
     * 创建数据库表
     */
    private void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // 称号表
            String createTitlesTable = """
                CREATE TABLE IF NOT EXISTS flashy_titles (
                    id VARCHAR(64) PRIMARY KEY,
                    raw TEXT NOT NULL,
                    price INT NOT NULL DEFAULT 0,
                    animated BOOLEAN NOT NULL DEFAULT FALSE,
                    permission VARCHAR(128),
                    description TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // 玩家拥有的称号表
            String createOwnedTable = """
                CREATE TABLE IF NOT EXISTS flashy_owned_titles (
                    player_uuid VARCHAR(36) NOT NULL,
                    title_id VARCHAR(64) NOT NULL,
                    obtained_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (player_uuid, title_id)
                )
            """;
            
            // 玩家装备的称号表
            String createEquippedTable = """
                CREATE TABLE IF NOT EXISTS flashy_equipped_titles (
                    player_uuid VARCHAR(36) PRIMARY KEY,
                    title_id VARCHAR(64),
                    equipped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // 玩家金币表
            String createCoinsTable = """
                CREATE TABLE IF NOT EXISTS flashy_coins (
                    player_uuid VARCHAR(36) PRIMARY KEY,
                    coins INT NOT NULL DEFAULT 0,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTitlesTable);
                stmt.execute(createOwnedTable);
                stmt.execute(createEquippedTable);
                stmt.execute(createCoinsTable);
            }
        }
    }
    
    /**
     * 加载缓存
     */
    public void loadCache() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // 加载称号
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM flashy_titles")) {
                while (rs.next()) {
                    Title title = new Title(
                        rs.getString("id"),
                        rs.getString("raw"),
                        rs.getInt("price"),
                        rs.getBoolean("animated"),
                        rs.getString("permission"),
                        rs.getString("description")
                    );
                    titleCache.put(title.getId(), title);
                }
            }
            
            // 加载拥有的称号
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM flashy_owned_titles")) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    String titleId = rs.getString("title_id");
                    ownedCache.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(titleId);
                }
            }
            
            // 加载装备的称号
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM flashy_equipped_titles WHERE title_id IS NOT NULL")) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    String titleId = rs.getString("title_id");
                    equippedCache.put(uuid, titleId);
                }
            }
            
            // 加载金币
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM flashy_coins")) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                    int coins = rs.getInt("coins");
                    coinsCache.put(uuid, coins);
                }
            }
        }
    }
    
    // ==================== 称号管理 ====================
    
    public CompletableFuture<Void> saveTitle(Title title) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql;
                if (config.getType().equalsIgnoreCase("mysql")) {
                    sql = """
                        INSERT INTO flashy_titles (id, raw, price, animated, permission, description)
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                        raw = VALUES(raw), price = VALUES(price), animated = VALUES(animated),
                        permission = VALUES(permission), description = VALUES(description)
                    """;
                } else {
                    // H2 和 SQLite 使用 MERGE 或 INSERT OR REPLACE
                    sql = """
                        MERGE INTO flashy_titles (id, raw, price, animated, permission, description)
                        VALUES (?, ?, ?, ?, ?, ?)
                    """;
                }
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, title.getId());
                    stmt.setString(2, title.getRaw());
                    stmt.setInt(3, title.getPrice());
                    stmt.setBoolean(4, title.isAnimated());
                    stmt.setString(5, title.getPermission());
                    stmt.setString(6, title.getDescription());
                    stmt.executeUpdate();
                }
                
                titleCache.put(title.getId(), title);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public CompletableFuture<Void> deleteTitle(String titleId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                // 删除称号
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM flashy_titles WHERE id = ?")) {
                    stmt.setString(1, titleId);
                    stmt.executeUpdate();
                }
                
                // 删除相关的拥有记录
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM flashy_owned_titles WHERE title_id = ?")) {
                    stmt.setString(1, titleId);
                    stmt.executeUpdate();
                }
                
                // 删除相关的装备记录
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE flashy_equipped_titles SET title_id = NULL WHERE title_id = ?")) {
                    stmt.setString(1, titleId);
                    stmt.executeUpdate();
                }
                
                titleCache.remove(titleId);
                ownedCache.values().forEach(set -> set.remove(titleId));
                equippedCache.entrySet().removeIf(entry -> titleId.equals(entry.getValue()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public Map<String, Title> getAllTitles() {
        return new HashMap<>(titleCache);
    }
    
    public Title getTitle(String id) {
        return titleCache.get(id);
    }
    
    // ==================== 玩家称号管理 ====================
    
    public CompletableFuture<Void> grantTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql;
                if (config.getType().equalsIgnoreCase("mysql")) {
                    sql = "INSERT IGNORE INTO flashy_owned_titles (player_uuid, title_id) VALUES (?, ?)";
                } else {
                    // H2 和 SQLite 使用 INSERT OR IGNORE
                    sql = "INSERT OR IGNORE INTO flashy_owned_titles (player_uuid, title_id) VALUES (?, ?)";
                }
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.setString(2, titleId);
                    stmt.executeUpdate();
                }
                
                ownedCache.computeIfAbsent(playerUuid, k -> ConcurrentHashMap.newKeySet()).add(titleId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public CompletableFuture<Void> revokeTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                // 删除拥有记录
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM flashy_owned_titles WHERE player_uuid = ? AND title_id = ?")) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.setString(2, titleId);
                    stmt.executeUpdate();
                }
                
                // 如果装备了这个称号，取消装备
                if (titleId.equals(equippedCache.get(playerUuid))) {
                    try (PreparedStatement stmt = conn.prepareStatement("UPDATE flashy_equipped_titles SET title_id = NULL WHERE player_uuid = ?")) {
                        stmt.setString(1, playerUuid.toString());
                        stmt.executeUpdate();
                    }
                    equippedCache.remove(playerUuid);
                }
                
                Set<String> owned = ownedCache.get(playerUuid);
                if (owned != null) {
                    owned.remove(titleId);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public Set<String> getOwnedTitles(UUID playerUuid) {
        return new HashSet<>(ownedCache.getOrDefault(playerUuid, Collections.emptySet()));
    }
    
    public boolean ownsTitle(UUID playerUuid, String titleId) {
        Set<String> owned = ownedCache.get(playerUuid);
        return owned != null && owned.contains(titleId);
    }
    
    // ==================== 装备称号管理 ====================
    
    public CompletableFuture<Void> equipTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = """
                    INSERT INTO flashy_equipped_titles (player_uuid, title_id, equipped_at) 
                    VALUES (?, ?, CURRENT_TIMESTAMP) 
                    ON DUPLICATE KEY UPDATE title_id = VALUES(title_id), equipped_at = VALUES(equipped_at)
                """;
                
                if (config.getType().equalsIgnoreCase("sqlite")) {
                    sql = """
                        INSERT OR REPLACE INTO flashy_equipped_titles (player_uuid, title_id, equipped_at) 
                        VALUES (?, ?, CURRENT_TIMESTAMP)
                    """;
                }
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.setString(2, titleId);
                    stmt.executeUpdate();
                }
                
                equippedCache.put(playerUuid, titleId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public CompletableFuture<Void> unequipTitle(UUID playerUuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE flashy_equipped_titles SET title_id = NULL WHERE player_uuid = ?")) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.executeUpdate();
                }
                
                equippedCache.remove(playerUuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public String getEquippedTitle(UUID playerUuid) {
        return equippedCache.get(playerUuid);
    }
    
    // ==================== 金币管理 ====================
    
    public CompletableFuture<Void> setCoins(UUID playerUuid, int coins) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql;
                if (config.getType().equalsIgnoreCase("mysql")) {
                    sql = """
                        INSERT INTO flashy_coins (player_uuid, coins, updated_at)
                        VALUES (?, ?, CURRENT_TIMESTAMP)
                        ON DUPLICATE KEY UPDATE coins = VALUES(coins), updated_at = VALUES(updated_at)
                    """;
                } else {
                    // H2 和 SQLite 使用 MERGE
                    sql = """
                        MERGE INTO flashy_coins (player_uuid, coins, updated_at)
                        VALUES (?, ?, CURRENT_TIMESTAMP)
                    """;
                }
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.setInt(2, Math.max(0, coins));
                    stmt.executeUpdate();
                }
                
                coinsCache.put(playerUuid, Math.max(0, coins));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public CompletableFuture<Void> addCoins(UUID playerUuid, int amount) {
        int currentCoins = getCoins(playerUuid);
        return setCoins(playerUuid, currentCoins + amount);
    }

    public int getCoins(UUID playerUuid) {
        return coinsCache.getOrDefault(playerUuid, 0);
    }

    /**
     * 购买称号事务（原子操作）
     */
    public CompletableFuture<Boolean> purchaseTitleTransaction(UUID playerUuid, String titleId, int price) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    // 1. 检查并扣除金币
                    String checkCoinsSQL = "SELECT coins FROM flashy_coins WHERE player_uuid = ?";
                    int currentCoins = 0;

                    try (PreparedStatement stmt = conn.prepareStatement(checkCoinsSQL)) {
                        stmt.setString(1, playerUuid.toString());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                currentCoins = rs.getInt("coins");
                            }
                        }
                    }

                    if (currentCoins < price) {
                        conn.rollback();
                        return false;
                    }

                    // 2. 扣除金币
                    String updateCoinsSQL;
                    if (config.getType().equalsIgnoreCase("mysql")) {
                        updateCoinsSQL = """
                            INSERT INTO flashy_coins (player_uuid, coins, updated_at)
                            VALUES (?, ?, CURRENT_TIMESTAMP)
                            ON DUPLICATE KEY UPDATE coins = VALUES(coins), updated_at = VALUES(updated_at)
                        """;
                    } else {
                        // H2 和 SQLite 使用 MERGE
                        updateCoinsSQL = """
                            MERGE INTO flashy_coins (player_uuid, coins, updated_at)
                            VALUES (?, ?, CURRENT_TIMESTAMP)
                        """;
                    }

                    try (PreparedStatement stmt = conn.prepareStatement(updateCoinsSQL)) {
                        stmt.setString(1, playerUuid.toString());
                        stmt.setInt(2, Math.max(0, currentCoins - price));
                        stmt.executeUpdate();
                    }

                    // 3. 给予称号
                    String grantTitleSQL;
                    if (config.getType().equalsIgnoreCase("mysql")) {
                        grantTitleSQL = "INSERT IGNORE INTO flashy_owned_titles (player_uuid, title_id) VALUES (?, ?)";
                    } else {
                        // H2 和 SQLite 使用 INSERT OR IGNORE
                        grantTitleSQL = "INSERT OR IGNORE INTO flashy_owned_titles (player_uuid, title_id) VALUES (?, ?)";
                    }

                    try (PreparedStatement stmt = conn.prepareStatement(grantTitleSQL)) {
                        stmt.setString(1, playerUuid.toString());
                        stmt.setString(2, titleId);
                        stmt.executeUpdate();
                    }

                    // 4. 提交事务
                    conn.commit();

                    // 5. 更新缓存
                    coinsCache.put(playerUuid, Math.max(0, currentCoins - price));
                    ownedCache.computeIfAbsent(playerUuid, k -> ConcurrentHashMap.newKeySet()).add(titleId);

                    return true;

                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }

            } catch (SQLException e) {
                throw new RuntimeException("购买称号事务失败", e);
            }
        });
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
