package top.redstarmc.plugin.velocitytitle.velocity.record;

import org.jetbrains.annotations.NotNull;

/**
 * 称号实例
 */
public record Title(@NotNull String name, @NotNull String display, @NotNull String description, boolean isPrefix) { }
