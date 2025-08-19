package org.example.flashytitles.velocity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.flashytitles.core.database.DatabaseConfig;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Velocity 配置管理器
 */
public class ConfigManager {
    
    private final Path dataDirectory;
    private final Path configFile;
    private final Logger logger;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private JsonObject config;
    
    public ConfigManager(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.configFile = dataDirectory.resolve("config.json");
        this.logger = logger;
    }
    
    public void loadConfig() throws IOException {
        // 创建数据目录
        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
        }

        // 如果配置文件不存在，创建默认配置
        if (!Files.exists(configFile)) {
            createDefaultConfig();
        }

        // 加载配置
        try (Reader reader = Files.newBufferedReader(configFile)) {
            config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            logger.error("配置文件读取失败，使用默认配置: {}", e.getMessage());
            // 备份损坏的配置文件
            try {
                Path backupFile = configFile.resolveSibling(configFile.getFileName() + ".backup");
                Files.copy(configFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("已备份损坏的配置文件到: {}", backupFile);
            } catch (IOException backupError) {
                logger.warn("无法备份损坏的配置文件: {}", backupError.getMessage());
            }
            // 重新创建默认配置
            createDefaultConfig();
        }

        if (config == null) {
            config = new JsonObject();
        }

        // 验证配置完整性
        validateConfig();

        logger.info("配置文件加载完成: {}", configFile);
    }
    
    private void createDefaultConfig() throws IOException {
        JsonObject defaultConfig = new JsonObject();
        
        // 数据库配置 (默认使用H2数据库)
        JsonObject database = new JsonObject();
        database.addProperty("type", "h2"); // h2, mysql
        database.addProperty("host", "localhost");
        database.addProperty("port", 3306);
        database.addProperty("database", "flashy_titles");
        database.addProperty("username", "root");
        database.addProperty("password", "password");
        database.addProperty("sqlite-file", "./data/flashytitles"); // H2数据库文件路径
        defaultConfig.add("database", database);
        
        // 称号配置
        JsonObject titles = new JsonObject();
        titles.addProperty("animation-interval", 10); // tick间隔
        titles.addProperty("max-title-length", 32);
        titles.addProperty("allow-color-codes", true);
        titles.addProperty("default-starting-coins", 100);
        defaultConfig.add("titles", titles);
        
        // 同步配置
        JsonObject sync = new JsonObject();
        sync.addProperty("enabled", true);
        sync.addProperty("sync-interval", 30); // 秒
        sync.addProperty("auto-sync-on-join", true);
        defaultConfig.add("sync", sync);
        
        // 消息配置
        JsonObject messages = new JsonObject();
        messages.addProperty("prefix", "§6[FlashyTitles] §r");
        
        JsonObject msgTexts = new JsonObject();
        msgTexts.addProperty("no-permission", "§c你没有权限执行此命令！");
        msgTexts.addProperty("title-not-found", "§c称号不存在！");
        msgTexts.addProperty("title-not-owned", "§c你没有拥有这个称号！");
        msgTexts.addProperty("insufficient-coins", "§c金币不足！");
        msgTexts.addProperty("purchase-success", "§a成功购买称号: §e{title}");
        msgTexts.addProperty("equip-success", "§a成功装备称号: §e{title}");
        msgTexts.addProperty("unequip-success", "§a已取消装备称号");
        msgTexts.addProperty("title-granted", "§a已获得称号: §e{title}");
        msgTexts.addProperty("title-revoked", "§c称号已被收回: §e{title}");
        messages.add("texts", msgTexts);
        
        defaultConfig.add("messages", messages);
        
        // 保存默认配置
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            gson.toJson(defaultConfig, writer);
        }
        
        config = defaultConfig;
        logger.info("已创建默认配置文件: {}", configFile);
    }

    /**
     * 验证配置完整性
     */
    private void validateConfig() {
        boolean needsUpdate = false;

        // 检查必需的配置项
        if (!config.has("database")) {
            logger.warn("配置文件缺少database配置，将使用默认值");
            needsUpdate = true;
        }

        if (!config.has("titles")) {
            logger.warn("配置文件缺少titles配置，将使用默认值");
            needsUpdate = true;
        }

        if (!config.has("sync")) {
            logger.warn("配置文件缺少sync配置，将使用默认值");
            needsUpdate = true;
        }

        // 如果需要更新，重新创建默认配置
        if (needsUpdate) {
            try {
                createDefaultConfig();
                logger.info("配置文件已更新为完整版本");
            } catch (IOException e) {
                logger.error("更新配置文件失败: {}", e.getMessage());
            }
        }
    }
    
    // 数据库配置
    public DatabaseConfig getDatabaseConfig() {
        JsonObject db = config.getAsJsonObject("database");
        return new DatabaseConfig(
            db.get("type").getAsString(),
            db.get("host").getAsString(),
            db.get("port").getAsInt(),
            db.get("database").getAsString(),
            db.get("username").getAsString(),
            db.get("password").getAsString(),
            db.get("sqlite-file").getAsString()
        );
    }
    
    public String getDatabaseType() {
        return config.getAsJsonObject("database").get("type").getAsString();
    }
    
    // 性能配置
    public int getCacheSize() {
        JsonObject perf = config.getAsJsonObject("performance");
        return perf != null ? perf.get("cache-size").getAsInt() : 1000;
    }

    public int getConnectionPoolSize() {
        JsonObject perf = config.getAsJsonObject("performance");
        return perf != null ? perf.get("connection-pool-size").getAsInt() : 10;
    }

    public int getQueryTimeout() {
        JsonObject perf = config.getAsJsonObject("performance");
        return perf != null ? perf.get("query-timeout").getAsInt() : 30;
    }
    
    // 称号配置
    public int getAnimationInterval() {
        return config.getAsJsonObject("titles").get("animation-interval").getAsInt();
    }
    
    public int getMaxTitleLength() {
        return config.getAsJsonObject("titles").get("max-title-length").getAsInt();
    }
    
    public boolean isColorCodesAllowed() {
        return config.getAsJsonObject("titles").get("allow-color-codes").getAsBoolean();
    }
    
    public int getDefaultStartingCoins() {
        return config.getAsJsonObject("titles").get("default-starting-coins").getAsInt();
    }

    public int getMaxPrice() {
        JsonObject titles = config.getAsJsonObject("titles");
        return titles.has("max-price") ? titles.get("max-price").getAsInt() : 1000000;
    }
    
    // 同步配置
    public boolean isSyncEnabled() {
        return config.getAsJsonObject("sync").get("enabled").getAsBoolean();
    }
    
    public int getSyncInterval() {
        return config.getAsJsonObject("sync").get("sync-interval").getAsInt();
    }
    
    public boolean isAutoSyncOnJoin() {
        return config.getAsJsonObject("sync").get("auto-sync-on-join").getAsBoolean();
    }
    
    // 消息配置
    public String getMessagePrefix() {
        return config.getAsJsonObject("messages").get("prefix").getAsString();
    }
    
    public String getMessage(String key) {
        JsonObject texts = config.getAsJsonObject("messages").getAsJsonObject("texts");
        return texts.has(key) ? texts.get(key).getAsString() : "§c消息未找到: " + key;
    }
    
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
    
    public String getMessage(String key, String placeholder, String value) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, value);
        return getMessage(key, placeholders);
    }
    
    public void saveConfig() throws IOException {
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            gson.toJson(config, writer);
        }
        logger.info("配置文件已保存");
    }
}
