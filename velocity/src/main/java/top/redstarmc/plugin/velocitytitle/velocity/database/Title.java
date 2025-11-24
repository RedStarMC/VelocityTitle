package top.redstarmc.plugin.velocitytitle.velocity.database;

import org.jetbrains.annotations.NotNull;

public record Title(@NotNull String name, @NotNull String display, @NotNull String description, @NotNull String type) {
}
