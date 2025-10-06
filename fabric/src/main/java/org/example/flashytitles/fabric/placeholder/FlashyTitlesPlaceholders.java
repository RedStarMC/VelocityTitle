package org.example.flashytitles.fabric.placeholder;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.example.flashytitles.fabric.manager.DisplayManager;

/**
 * FlashyTitles Fabric 占位符扩展
 * 使用 Text Placeholder API
 */
public class FlashyTitlesPlaceholders {
    
    private final DisplayManager displayManager;
    
    public FlashyTitlesPlaceholders(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }
    
    /**
     * 注册占位符
     */
    public void register() {
        // %flashytitles:title%
        Placeholders.register(Identifier.of("flashytitles", "title"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.value(Text.empty());
            }
            
            String titleText = displayManager.getPlayerTitleText(ctx.player().getUuid());
            return PlaceholderResult.value(titleText != null ? Text.literal(titleText) : Text.empty());
        });
        
        // %flashytitles:title_raw%
        Placeholders.register(Identifier.of("flashytitles", "title_raw"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.value(Text.empty());
            }
            
            String titleText = displayManager.getPlayerTitleText(ctx.player().getUuid());
            return PlaceholderResult.value(titleText != null ? Text.literal(titleText) : Text.empty());
        });
        
        // %flashytitles:title_id%
        Placeholders.register(Identifier.of("flashytitles", "title_id"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.value(Text.empty());
            }
            
            String titleId = displayManager.getPlayerTitleId(ctx.player().getUuid());
            return PlaceholderResult.value(titleId != null ? Text.literal(titleId) : Text.empty());
        });
        
        // %flashytitles:has_title%
        Placeholders.register(Identifier.of("flashytitles", "has_title"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.value(Text.literal("false"));
            }
            
            boolean hasTitle = displayManager.hasTitle(ctx.player().getUuid());
            return PlaceholderResult.value(Text.literal(hasTitle ? "true" : "false"));
        });
        
        // %flashytitles:title_with_space%
        Placeholders.register(Identifier.of("flashytitles", "title_with_space"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.value(Text.empty());
            }
            
            String titleText = displayManager.getPlayerTitleText(ctx.player().getUuid());
            if (titleText != null && !titleText.isEmpty()) {
                return PlaceholderResult.value(Text.literal(titleText + " "));
            }
            return PlaceholderResult.value(Text.empty());
        });
        
        // %flashytitles:title_prefix%
        Placeholders.register(Identifier.of("flashytitles", "title_prefix"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.value(Text.empty());
            }
            
            String titleText = displayManager.getPlayerTitleText(ctx.player().getUuid());
            return PlaceholderResult.value(titleText != null ? Text.literal(titleText) : Text.empty());
        });
    }
    
    /**
     * 注销占位符
     * 注意：Text Placeholder API 2.4.1版本可能不支持unregister方法
     */
    public void unregister() {
        // Text Placeholder API 2.4.1版本可能不支持动态注销
        // 占位符会在模组卸载时自动清理
    }
}
