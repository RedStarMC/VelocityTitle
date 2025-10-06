package org.example.flashytitles.neoforge.placeholder;

import org.example.flashytitles.neoforge.manager.DisplayManager;

import java.util.UUID;

/**
 * FlashyTitles NeoForge 内置占位符服务
 * 提供简单的占位符处理功能
 */
public class PlaceholderService {
    
    private final DisplayManager displayManager;
    
    public PlaceholderService(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }
    
    /**
     * 处理占位符
     * @param playerUuid 玩家UUID
     * @param text 包含占位符的文本
     * @return 处理后的文本
     */
    public String processPlaceholders(UUID playerUuid, String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String result = text;
        
        // 处理所有支持的占位符
        result = result.replace("%flashytitles_title%", 
                displayManager.processPlaceholder(playerUuid, "flashytitles_title"));
        result = result.replace("%flashytitles_title_raw%", 
                displayManager.processPlaceholder(playerUuid, "flashytitles_title_raw"));
        result = result.replace("%flashytitles_title_id%", 
                displayManager.processPlaceholder(playerUuid, "flashytitles_title_id"));
        result = result.replace("%flashytitles_has_title%", 
                displayManager.processPlaceholder(playerUuid, "flashytitles_has_title"));
        result = result.replace("%flashytitles_title_with_space%", 
                displayManager.processPlaceholder(playerUuid, "flashytitles_title_with_space"));
        result = result.replace("%flashytitles_title_prefix%", 
                displayManager.processPlaceholder(playerUuid, "flashytitles_title_prefix"));
        
        return result;
    }
    
    /**
     * 获取单个占位符的值
     * @param playerUuid 玩家UUID
     * @param placeholder 占位符名称（不包含%）
     * @return 占位符值
     */
    public String getPlaceholderValue(UUID playerUuid, String placeholder) {
        return displayManager.processPlaceholder(playerUuid, placeholder);
    }
    
    /**
     * 检查是否是FlashyTitles的占位符
     * @param placeholder 占位符文本
     * @return 是否是FlashyTitles占位符
     */
    public boolean isFlashyTitlesPlaceholder(String placeholder) {
        if (placeholder == null) {
            return false;
        }
        
        String lower = placeholder.toLowerCase();
        return lower.startsWith("%flashytitles_") && lower.endsWith("%");
    }
    
    /**
     * 获取支持的占位符列表
     * @return 占位符列表
     */
    public String[] getSupportedPlaceholders() {
        return new String[] {
            "%flashytitles_title%",
            "%flashytitles_title_raw%", 
            "%flashytitles_title_id%",
            "%flashytitles_has_title%",
            "%flashytitles_title_with_space%",
            "%flashytitles_title_prefix%"
        };
    }
}
