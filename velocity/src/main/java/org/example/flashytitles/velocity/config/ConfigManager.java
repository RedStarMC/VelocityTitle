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
        }
        
        if (config == null) {
            config = new JsonObject();
        }
        
        logger.info("配置文件加载完成: {}", configFile);
    }
    
    private void createDefaultConfig() throws IOException {
        JsonObject defaultConfig = new JsonObject();
        
        // 数据库配置 (使用H2数据库，兼容GitHub原仓库设计)
        JsonObject database = new JsonObject();
        database.addProperty("type", "h2"); // h2, mysql 或 sqlite
        database.addProperty("host", "localhost");
        database.addProperty("port", 3306);
        database.addProperty("database", "flashy_titles");
        database.addProperty("username", "root");
        database.addProperty("password", "password");
        database.addProperty("sqlite-file", "titles.db");
        defaultConfig.add("database", database);
        
        // Redis 配置 (用于实时同步)
        JsonObject redis = new JsonObject();
        redis.addProperty("enabled", false);
        redis.addProperty("host", "localhost");
        redis.addProperty("port", 6379);
        redis.addProperty("password", "");
        redis.addProperty("database", 0);
        defaultConfig.add("redis", redis);
        
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
    
    // Redis 配置
    public boolean isRedisEnabled() {
        return config.getAsJsonObject("redis").get("enabled").getAsBoolean();
    }
    
    public String getRedisHost() {
        return config.getAsJsonObject("redis").get("host").getAsString();
    }
    
    public int getRedisPort() {
        return config.getAsJsonObject("redis").get("port").getAsInt();
    }
    
    public String getRedisPassword() {
        return config.getAsJsonObject("redis").get("password").getAsString();
    }
    
    public int getRedisDatabase() {
        return config.getAsJsonObject("redis").get("database").getAsInt();
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
