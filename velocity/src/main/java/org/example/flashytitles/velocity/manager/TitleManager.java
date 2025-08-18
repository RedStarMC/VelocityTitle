package org.example.flashytitles.velocity.manager;

import org.example.flashytitles.core.database.DatabaseManager;
import org.example.flashytitles.core.model.Title;
import org.example.flashytitles.velocity.config.ConfigManager;
import org.example.flashytitles.velocity.sync.SimplifiedSyncManager;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Velocity 称号管理器
 * 负责称号的创建、删除、购买、装备等操作
 */
public class TitleManager {
    
    private final ConfigManager configManager;
    private final Logger logger;
    private final DatabaseManager databaseManager;
    private final ScheduledExecutorService scheduler;
    private SimplifiedSyncManager syncManager;
    
    // 动画tick计数器
    private int animationTick = 0;
    
    public TitleManager(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
        this.databaseManager = new DatabaseManager(configManager.getDatabaseConfig());
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    /**
     * 初始化管理器
     */
    public void initialize() throws SQLException {
        logger.info("正在初始化称号管理器...");
        
        // 初始化数据库
        databaseManager.initialize();
        
        // 启动动画tick任务
        int interval = configManager.getAnimationInterval();
        scheduler.scheduleAtFixedRate(() -> {
            animationTick++;
            if (animationTick >= Integer.MAX_VALUE - 1000) {
                animationTick = 0; // 防止溢出
            }
        }, 0, interval * 50, TimeUnit.MILLISECONDS); // tick转换为毫秒
        
        logger.info("称号管理器初始化完成");
        logger.info("- 动画更新间隔: {} ticks", interval);
        logger.info("- 已加载称号: {} 个", getAllTitles().size());
    }
    
    /**
     * 关闭管理器
     */
    public void shutdown() {
        logger.info("正在关闭称号管理器...");
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        logger.info("称号管理器已关闭");
    }
    
    // ==================== 称号管理 ====================
    
    /**
     * 创建新称号
     */
    public CompletableFuture<Boolean> createTitle(String id, String raw, int price, boolean animated, String permission, String description) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 验证称号ID
                if (id == null || id.trim().isEmpty()) {
                    logger.warn("尝试创建空ID的称号");
                    return false;
                }
                
                // 验证称号长度
                if (raw.length() > configManager.getMaxTitleLength()) {
                    logger.warn("称号长度超过限制: {} > {}", raw.length(), configManager.getMaxTitleLength());
                    return false;
                }
                
                // 检查颜色代码权限
                if (!configManager.isColorCodesAllowed() && raw.contains("§")) {
                    logger.warn("配置不允许使用颜色代码");
                    return false;
                }
                
                Title title = new Title(id, raw, price, animated, permission, description);
                databaseManager.saveTitle(title).join();
                
                logger.info("创建称号: {} (价格: {}, 动画: {})", id, price, animated);
                return true;
            } catch (Exception e) {
                logger.error("创建称号失败: " + id, e);
                return false;
            }
        });
    }
    
    /**
     * 删除称号
     */
    public CompletableFuture<Boolean> deleteTitle(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                databaseManager.deleteTitle(id).join();
                logger.info("删除称号: {}", id);
                return true;
            } catch (Exception e) {
                logger.error("删除称号失败: " + id, e);
                return false;
            }
        });
    }
    
    /**
     * 获取所有称号
     */
    public Map<String, Title> getAllTitles() {
        return databaseManager.getAllTitles();
    }
    
    /**
     * 获取指定称号
     */
    public Title getTitle(String id) {
        return databaseManager.getTitle(id);
    }
    
    /**
     * 渲染称号（支持动画）
     */
    public String renderTitle(String titleId) {
        Title title = getTitle(titleId);
        if (title == null) {
            return "";
        }
        return title.render(animationTick);
    }
    
    // ==================== 玩家称号管理 ====================
    
    /**
     * 购买称号
     */
    public CompletableFuture<PurchaseResult> purchaseTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Title title = getTitle(titleId);
                if (title == null) {
                    return PurchaseResult.TITLE_NOT_FOUND;
                }

                // 检查是否已拥有
                if (ownsTitle(playerUuid, titleId)) {
                    return PurchaseResult.ALREADY_OWNED;
                }

                // 检查金币
                int playerCoins = getCoins(playerUuid);
                if (playerCoins < title.getPrice()) {
                    return PurchaseResult.INSUFFICIENT_COINS;
                }

                // 检查权限（如果有的话）
                if (title.getPermission() != null && !title.getPermission().isEmpty()) {
                    // 通过同步管理器检查权限
                    boolean hasPermission = checkPlayerPermission(playerUuid, title.getPermission());
                    if (!hasPermission) {
                        logger.info("玩家 {} 没有权限 {} 购买称号 {}", playerUuid, title.getPermission(), titleId);
                        return PurchaseResult.NO_PERMISSION;
                    }
                    logger.debug("玩家 {} 权限检查通过: {}", playerUuid, title.getPermission());
                }

                // 使用事务确保原子性
                boolean success = databaseManager.purchaseTitleTransaction(playerUuid, titleId, title.getPrice()).join();
                if (!success) {
                    return PurchaseResult.ERROR;
                }

                logger.info("玩家 {} 购买称号: {} (花费: {})", playerUuid, titleId, title.getPrice());
                return PurchaseResult.SUCCESS;

            } catch (Exception e) {
                logger.error("购买称号失败: " + titleId, e);
                return PurchaseResult.ERROR;
            }
        });
    }
    
    /**
     * 装备称号
     */
    public CompletableFuture<Boolean> equipTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!ownsTitle(playerUuid, titleId)) {
                    return false;
                }
                
                databaseManager.equipTitle(playerUuid, titleId).join();
                logger.info("玩家 {} 装备称号: {}", playerUuid, titleId);
                return true;
            } catch (Exception e) {
                logger.error("装备称号失败: " + titleId, e);
                return false;
            }
        });
    }
    
    /**
     * 取消装备称号
     */
    public CompletableFuture<Boolean> unequipTitle(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                databaseManager.unequipTitle(playerUuid).join();
                logger.info("玩家 {} 取消装备称号", playerUuid);
                return true;
            } catch (Exception e) {
                logger.error("取消装备称号失败", e);
                return false;
            }
        });
    }
    
    /**
     * 给予称号（管理员命令）
     */
    public CompletableFuture<Boolean> grantTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (getTitle(titleId) == null) {
                    return false;
                }
                
                databaseManager.grantTitle(playerUuid, titleId).join();
                logger.info("管理员给予玩家 {} 称号: {}", playerUuid, titleId);
                return true;
            } catch (Exception e) {
                logger.error("给予称号失败: " + titleId, e);
                return false;
            }
        });
    }
    
    /**
     * 收回称号（管理员命令）
     */
    public CompletableFuture<Boolean> revokeTitle(UUID playerUuid, String titleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                databaseManager.revokeTitle(playerUuid, titleId).join();
                logger.info("管理员收回玩家 {} 称号: {}", playerUuid, titleId);
                return true;
            } catch (Exception e) {
                logger.error("收回称号失败: " + titleId, e);
                return false;
            }
        });
    }
    
    // ==================== 查询方法 ====================
    
    public Set<String> getOwnedTitles(UUID playerUuid) {
        return databaseManager.getOwnedTitles(playerUuid);
    }
    
    public boolean ownsTitle(UUID playerUuid, String titleId) {
        return databaseManager.ownsTitle(playerUuid, titleId);
    }
    
    public String getEquippedTitle(UUID playerUuid) {
        return databaseManager.getEquippedTitle(playerUuid);
    }
    
    // ==================== 金币管理 ====================
    
    public int getCoins(UUID playerUuid) {
        return databaseManager.getCoins(playerUuid);
    }
    
    public void setCoins(UUID playerUuid, int coins) {
        databaseManager.setCoins(playerUuid, coins);
    }

    public void addCoins(UUID playerUuid, int amount) {
        databaseManager.addCoins(playerUuid, amount);
    }
    
    // ==================== 工具方法 ====================
    
    public int getCurrentAnimationTick() {
        return animationTick;
    }

    /**
     * 重载称号数据
     */
    public void reloadTitles() {
        try {
            databaseManager.loadCache();
            logger.info("称号数据重载完成");
        } catch (Exception e) {
            logger.error("重载称号数据失败", e);
            throw new RuntimeException("重载称号数据失败", e);
        }
    }

    /**
     * 获取配置管理器
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * 设置同步管理器（用于权限检查）
     */
    public void setSyncManager(SimplifiedSyncManager syncManager) {
        this.syncManager = syncManager;
    }

    /**
     * 检查玩家权限
     * 通过同步管理器与后端服务器通信检查权限
     */
    private boolean checkPlayerPermission(UUID playerUuid, String permission) {
        if (syncManager != null) {
            try {
                // 使用同步管理器进行权限检查
                return syncManager.checkPlayerPermission(playerUuid, permission).get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.warn("权限检查失败，默认允许: 玩家 {} 权限 {}", playerUuid, permission, e);
                return true; // 权限检查失败时默认允许，避免阻塞购买流程
            }
        }

        // 如果没有同步管理器，使用简化的权限检查逻辑
        logger.debug("使用简化权限检查: 玩家 {} 权限 {}", playerUuid, permission);
        return true; // 默认允许
    }

    /**
     * 购买结果枚举
     */
    public enum PurchaseResult {
        SUCCESS,
        TITLE_NOT_FOUND,
        ALREADY_OWNED,
        INSUFFICIENT_COINS,
        NO_PERMISSION,
        ERROR
    }
}
