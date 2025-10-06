package org.example.flashytitles.spigot.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.example.flashytitles.spigot.FlashyTitlesSpigot;
import org.example.flashytitles.spigot.manager.DisplayManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * FlashyTitles PlaceholderAPI 扩展
 */
public class FlashyTitlesExpansion extends PlaceholderExpansion {
    
    private final FlashyTitlesSpigot plugin;
    private final DisplayManager displayManager;
    
    public FlashyTitlesExpansion(FlashyTitlesSpigot plugin, DisplayManager displayManager) {
        this.plugin = plugin;
        this.displayManager = displayManager;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "flashytitles";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "maple";
    }
    
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true; // 插件重载时保持注册
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) {
            return "";
        }
        
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return "";
        }
        
        return switch (params.toLowerCase()) {
            case "title" -> {
                // 返回玩家当前装备的称号
                String titleText = displayManager.getPlayerTitleText(onlinePlayer.getUniqueId());
                yield titleText != null ? titleText : "";
            }
            case "title_raw" -> {
                // 返回原始称号文本（不带颜色代码处理）
                String titleText = displayManager.getPlayerTitleText(onlinePlayer.getUniqueId());
                yield titleText != null ? titleText : "";
            }
            case "title_id" -> {
                // 返回称号ID
                String titleId = displayManager.getPlayerTitleId(onlinePlayer.getUniqueId());
                yield titleId != null ? titleId : "";
            }
            case "has_title" -> {
                // 返回是否有称号
                boolean hasTitle = displayManager.hasTitle(onlinePlayer.getUniqueId());
                yield hasTitle ? "true" : "false";
            }
            case "title_with_space" -> {
                // 返回带空格的称号（如果有称号则在后面加空格）
                String titleText = displayManager.getPlayerTitleText(onlinePlayer.getUniqueId());
                yield titleText != null && !titleText.isEmpty() ? titleText + " " : "";
            }
            case "title_prefix" -> {
                // 返回称号作为前缀（兼容其他插件）
                String titleText = displayManager.getPlayerTitleText(onlinePlayer.getUniqueId());
                yield titleText != null ? titleText : "";
            }
            default -> {
                // 检查是否是动态参数
                if (params.startsWith("title_")) {
                    // 可以扩展更多参数
                    yield "";
                }
                yield null; // 未知参数
            }
        };
    }
    
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }
}
