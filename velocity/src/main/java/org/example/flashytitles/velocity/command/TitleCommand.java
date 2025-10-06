package org.example.flashytitles.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.example.flashytitles.core.model.Title;
import org.example.flashytitles.velocity.manager.TitleManager;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 称号命令处理器
 */
public class TitleCommand implements SimpleCommand {
    
    private final TitleManager titleManager;
    private final ProxyServer server;
    private final Logger logger;
    
    public TitleCommand(TitleManager titleManager, ProxyServer server, Logger logger) {
        this.titleManager = titleManager;
        this.server = server;
        this.logger = logger;
    }
    
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        
        if (args.length == 0) {
            sendHelp(source);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "shop" -> handleShop(source);
            case "buy" -> handleBuy(source, args);
            case "equip" -> handleEquip(source, args);
            case "unequip" -> handleUnequip(source);
            case "list" -> handleList(source);
            case "coins" -> handleCoins(source, args);
            case "create" -> handleCreate(source, args);
            case "delete" -> handleDelete(source, args);
            case "give" -> handleGive(source, args);
            case "revoke" -> handleRevoke(source, args);
            case "reload" -> handleReload(source);
            case "help" -> sendHelp(source);
            default -> source.sendMessage(Component.text("未知命令！使用 /title help 查看帮助", NamedTextColor.RED));
        }
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length <= 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.addAll(Arrays.asList("shop", "buy", "equip", "unequip", "list", "coins", "help"));
            
            // 管理员命令
            if (invocation.source().hasPermission("flashytitles.admin")) {
                suggestions.addAll(Arrays.asList("create", "delete", "give", "revoke", "reload"));
            }
            
            return suggestions;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "buy", "equip" -> {
                // 建议可购买/装备的称号
                return new ArrayList<>(titleManager.getAllTitles().keySet());
            }
            case "delete", "give", "revoke" -> {
                if (invocation.source().hasPermission("flashytitles.admin")) {
                    return new ArrayList<>(titleManager.getAllTitles().keySet());
                }
            }
            case "coins" -> {
                if (args.length == 2) {
                    return Arrays.asList("get", "add", "set");
                }
            }
        }
        
        return Collections.emptyList();
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("flashytitles.use");
    }
    
    // ==================== 命令处理方法 ====================
    
    private void handleShop(CommandSource source) {
        Map<String, Title> titles = titleManager.getAllTitles();
        
        if (titles.isEmpty()) {
            source.sendMessage(Component.text("商城暂时没有称号出售", NamedTextColor.YELLOW));
            return;
        }
        
        source.sendMessage(Component.text("=== 称号商城 ===", NamedTextColor.GOLD));
        
        for (Title title : titles.values()) {
            String displayText = title.getDisplayText();
            Component message = Component.text("• ", NamedTextColor.GRAY)
                .append(Component.text(title.getId(), NamedTextColor.YELLOW))
                .append(Component.text(" - ", NamedTextColor.GRAY))
                .append(Component.text(title.getPrice() + " 金币", NamedTextColor.GREEN))
                .append(Component.text(" | 预览: ", NamedTextColor.GRAY))
                .append(Component.text(displayText, NamedTextColor.WHITE));
            
            source.sendMessage(message);
            
            if (!title.getDescription().isEmpty()) {
                source.sendMessage(Component.text("  " + title.getDescription(), NamedTextColor.GRAY));
            }
        }
    }
    
    private void handleBuy(CommandSource source, String[] args) {
        if (!(source instanceof Player player)) {
            source.sendMessage(Component.text("只有玩家可以购买称号", NamedTextColor.RED));
            return;
        }
        
        if (args.length < 2) {
            source.sendMessage(Component.text("用法: /title buy <称号ID>", NamedTextColor.RED));
            return;
        }
        
        String titleId = args[1];
        
        titleManager.purchaseTitle(player.getUniqueId(), titleId).thenAccept(result -> {
            switch (result) {
                case SUCCESS -> {
                    source.sendMessage(Component.text("成功购买称号: " + titleId, NamedTextColor.GREEN));
                    source.sendMessage(Component.text("使用 /title equip " + titleId + " 来装备", NamedTextColor.YELLOW));
                }
                case TITLE_NOT_FOUND -> source.sendMessage(Component.text("称号不存在: " + titleId, NamedTextColor.RED));
                case ALREADY_OWNED -> source.sendMessage(Component.text("你已经拥有这个称号了", NamedTextColor.YELLOW));
                case INSUFFICIENT_COINS -> {
                    Title title = titleManager.getTitle(titleId);
                    int needed = title != null ? title.getPrice() : 0;
                    int current = titleManager.getCoins(player.getUniqueId());
                    source.sendMessage(Component.text("金币不足！需要: " + needed + ", 当前: " + current, NamedTextColor.RED));
                }
                case NO_PERMISSION -> source.sendMessage(Component.text("你没有权限购买这个称号", NamedTextColor.RED));
                case ERROR -> source.sendMessage(Component.text("购买失败，请稍后重试", NamedTextColor.RED));
            }
        });
    }
    
    private void handleEquip(CommandSource source, String[] args) {
        if (!(source instanceof Player player)) {
            source.sendMessage(Component.text("只有玩家可以装备称号", NamedTextColor.RED));
            return;
        }
        
        if (args.length < 2) {
            source.sendMessage(Component.text("用法: /title equip <称号ID>", NamedTextColor.RED));
            return;
        }
        
        String titleId = args[1];
        
        titleManager.equipTitle(player.getUniqueId(), titleId).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("成功装备称号: " + titleId, NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("装备失败！请确认你拥有这个称号", NamedTextColor.RED));
            }
        });
    }
    
    private void handleUnequip(CommandSource source) {
        if (!(source instanceof Player player)) {
            source.sendMessage(Component.text("只有玩家可以取消装备称号", NamedTextColor.RED));
            return;
        }
        
        titleManager.unequipTitle(player.getUniqueId()).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("已取消装备称号", NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("取消装备失败", NamedTextColor.RED));
            }
        });
    }
    
    private void handleList(CommandSource source) {
        if (!(source instanceof Player player)) {
            source.sendMessage(Component.text("只有玩家可以查看拥有的称号", NamedTextColor.RED));
            return;
        }
        
        Set<String> ownedTitles = titleManager.getOwnedTitles(player.getUniqueId());
        String equippedTitle = titleManager.getEquippedTitle(player.getUniqueId());
        
        if (ownedTitles.isEmpty()) {
            source.sendMessage(Component.text("你还没有任何称号", NamedTextColor.YELLOW));
            return;
        }
        
        source.sendMessage(Component.text("=== 你的称号 ===", NamedTextColor.GOLD));
        
        for (String titleId : ownedTitles) {
            Title title = titleManager.getTitle(titleId);
            if (title != null) {
                Component message = Component.text("• ", NamedTextColor.GRAY)
                    .append(Component.text(titleId, NamedTextColor.YELLOW))
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text(title.getDisplayText(), NamedTextColor.WHITE));
                
                if (titleId.equals(equippedTitle)) {
                    message = message.append(Component.text(" [已装备]", NamedTextColor.GREEN));
                }
                
                source.sendMessage(message);
            }
        }
        
        source.sendMessage(Component.text("当前金币: " + titleManager.getCoins(player.getUniqueId()), NamedTextColor.AQUA));
    }
    
    private void handleCoins(CommandSource source, String[] args) {
        if (args.length < 2) {
            if (source instanceof Player player) {
                int coins = titleManager.getCoins(player.getUniqueId());
                source.sendMessage(Component.text("你的金币: " + coins, NamedTextColor.AQUA));
            } else {
                source.sendMessage(Component.text("用法: /title coins <get|add|set> [玩家] [数量]", NamedTextColor.RED));
            }
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "get" -> {
                if (source instanceof Player player) {
                    int coins = titleManager.getCoins(player.getUniqueId());
                    source.sendMessage(Component.text("你的金币: " + coins, NamedTextColor.AQUA));
                } else {
                    source.sendMessage(Component.text("控制台需要指定玩家名", NamedTextColor.RED));
                }
            }
            case "add", "set" -> {
                if (!source.hasPermission("flashytitles.admin")) {
                    source.sendMessage(Component.text("你没有权限执行此命令", NamedTextColor.RED));
                    return;
                }
                
                if (args.length < 4) {
                    source.sendMessage(Component.text("用法: /title coins " + action + " <玩家> <数量>", NamedTextColor.RED));
                    return;
                }
                
                String playerName = args[2];
                Optional<Player> targetPlayer = server.getPlayer(playerName);
                
                if (targetPlayer.isEmpty()) {
                    source.sendMessage(Component.text("玩家不在线: " + playerName, NamedTextColor.RED));
                    return;
                }
                
                try {
                    int amount = Integer.parseInt(args[3]);
                    Player target = targetPlayer.get();
                    
                    if (action.equals("add")) {
                        titleManager.addCoins(target.getUniqueId(), amount);
                        source.sendMessage(Component.text("已为 " + playerName + " 增加 " + amount + " 金币", NamedTextColor.GREEN));
                    } else {
                        titleManager.setCoins(target.getUniqueId(), amount);
                        source.sendMessage(Component.text("已将 " + playerName + " 的金币设为 " + amount, NamedTextColor.GREEN));
                    }
                    
                    target.sendMessage(Component.text("你的金币已更新: " + titleManager.getCoins(target.getUniqueId()), NamedTextColor.AQUA));
                    
                } catch (NumberFormatException e) {
                    source.sendMessage(Component.text("无效的数字: " + args[3], NamedTextColor.RED));
                }
            }
        }
    }
    
    // ==================== 管理员命令 ====================
    
    private void handleCreate(CommandSource source, String[] args) {
        if (!source.hasPermission("flashytitles.admin")) {
            source.sendMessage(Component.text("你没有权限执行此命令", NamedTextColor.RED));
            return;
        }
        
        if (args.length < 4) {
            source.sendMessage(Component.text("用法: /title create <ID> <显示文本> <价格> [动画:true/false] [权限] [描述]", NamedTextColor.RED));
            return;
        }
        
        String id = args[1];
        String raw = args[2];

        // 验证称号ID格式
        if (!id.matches("^[a-zA-Z0-9_-]+$")) {
            source.sendMessage(Component.text("称号ID只能包含字母、数字、下划线和连字符", NamedTextColor.RED));
            return;
        }

        // 验证称号ID长度
        if (id.length() > 32) {
            source.sendMessage(Component.text("称号ID长度不能超过32个字符", NamedTextColor.RED));
            return;
        }

        // 验证称号文本长度
        if (raw.length() > 128) {
            source.sendMessage(Component.text("称号文本长度不能超过128个字符", NamedTextColor.RED));
            return;
        }

        try {
            int price = Integer.parseInt(args[3]);

            // 验证价格范围
            if (price < 0) {
                source.sendMessage(Component.text("价格不能为负数", NamedTextColor.RED));
                return;
            }

            if (price > 1000000) {
                source.sendMessage(Component.text("价格不能超过1,000,000", NamedTextColor.RED));
                return;
            }

            boolean animated = args.length > 4 && Boolean.parseBoolean(args[4]);
            String permission = args.length > 5 ? args[5] : null;
            String description = args.length > 6 ? String.join(" ", Arrays.copyOfRange(args, 6, args.length)) : "";

            // 验证描述长度
            if (description.length() > 256) {
                source.sendMessage(Component.text("描述长度不能超过256个字符", NamedTextColor.RED));
                return;
            }

            // 检查称号是否已存在
            if (titleManager.getTitle(id) != null) {
                source.sendMessage(Component.text("称号ID已存在: " + id, NamedTextColor.RED));
                return;
            }

            titleManager.createTitle(id, raw, price, animated, permission, description).thenAccept(success -> {
                if (success) {
                    source.sendMessage(Component.text("成功创建称号: " + id, NamedTextColor.GREEN));
                } else {
                    source.sendMessage(Component.text("创建称号失败，请检查日志获取详细信息", NamedTextColor.RED));
                }
            });

        } catch (NumberFormatException e) {
            source.sendMessage(Component.text("无效的价格格式: " + args[3] + "，请输入有效的数字", NamedTextColor.RED));
        }
    }
    
    private void handleDelete(CommandSource source, String[] args) {
        if (!source.hasPermission("flashytitles.admin")) {
            source.sendMessage(Component.text("你没有权限执行此命令", NamedTextColor.RED));
            return;
        }
        
        if (args.length < 2) {
            source.sendMessage(Component.text("用法: /title delete <称号ID>", NamedTextColor.RED));
            return;
        }
        
        String titleId = args[1];
        
        titleManager.deleteTitle(titleId).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("成功删除称号: " + titleId, NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("删除称号失败", NamedTextColor.RED));
            }
        });
    }
    
    private void handleGive(CommandSource source, String[] args) {
        if (!source.hasPermission("flashytitles.admin")) {
            source.sendMessage(Component.text("你没有权限执行此命令", NamedTextColor.RED));
            return;
        }
        
        if (args.length < 3) {
            source.sendMessage(Component.text("用法: /title give <玩家> <称号ID>", NamedTextColor.RED));
            return;
        }
        
        String playerName = args[1];
        String titleId = args[2];
        
        Optional<Player> targetPlayer = server.getPlayer(playerName);
        if (targetPlayer.isEmpty()) {
            source.sendMessage(Component.text("玩家不在线: " + playerName, NamedTextColor.RED));
            return;
        }
        
        Player target = targetPlayer.get();
        
        titleManager.grantTitle(target.getUniqueId(), titleId).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("成功给予 " + playerName + " 称号: " + titleId, NamedTextColor.GREEN));
                target.sendMessage(Component.text("你获得了新称号: " + titleId, NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("给予称号失败", NamedTextColor.RED));
            }
        });
    }
    
    private void handleRevoke(CommandSource source, String[] args) {
        if (!source.hasPermission("flashytitles.admin")) {
            source.sendMessage(Component.text("你没有权限执行此命令", NamedTextColor.RED));
            return;
        }
        
        if (args.length < 3) {
            source.sendMessage(Component.text("用法: /title revoke <玩家> <称号ID>", NamedTextColor.RED));
            return;
        }
        
        String playerName = args[1];
        String titleId = args[2];
        
        Optional<Player> targetPlayer = server.getPlayer(playerName);
        if (targetPlayer.isEmpty()) {
            source.sendMessage(Component.text("玩家不在线: " + playerName, NamedTextColor.RED));
            return;
        }
        
        Player target = targetPlayer.get();
        
        titleManager.revokeTitle(target.getUniqueId(), titleId).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("成功收回 " + playerName + " 的称号: " + titleId, NamedTextColor.GREEN));
                target.sendMessage(Component.text("你的称号被收回: " + titleId, NamedTextColor.YELLOW));
            } else {
                source.sendMessage(Component.text("收回称号失败", NamedTextColor.RED));
            }
        });
    }
    
    private void handleReload(CommandSource source) {
        if (!source.hasPermission("flashytitles.admin")) {
            source.sendMessage(Component.text("你没有权限执行此命令", NamedTextColor.RED));
            return;
        }

        source.sendMessage(Component.text("正在重载配置...", NamedTextColor.YELLOW));

        CompletableFuture.runAsync(() -> {
            try {
                // 重载配置文件
                titleManager.getConfigManager().loadConfig();

                // 重新加载称号缓存
                titleManager.reloadTitles();

                source.sendMessage(Component.text("配置重载完成！", NamedTextColor.GREEN));
                logger.info("管理员 {} 重载了配置", source.toString());

            } catch (Exception e) {
                source.sendMessage(Component.text("配置重载失败: " + e.getMessage(), NamedTextColor.RED));
                logger.error("配置重载失败", e);
            }
        });
    }
    
    private void sendHelp(CommandSource source) {
        source.sendMessage(Component.text("=== FlashyTitles 帮助 ===", NamedTextColor.GOLD));
        source.sendMessage(Component.text("/title shop - 查看称号商城", NamedTextColor.YELLOW));
        source.sendMessage(Component.text("/title buy <ID> - 购买称号", NamedTextColor.YELLOW));
        source.sendMessage(Component.text("/title equip <ID> - 装备称号", NamedTextColor.YELLOW));
        source.sendMessage(Component.text("/title unequip - 取消装备称号", NamedTextColor.YELLOW));
        source.sendMessage(Component.text("/title list - 查看拥有的称号", NamedTextColor.YELLOW));
        source.sendMessage(Component.text("/title coins - 查看金币", NamedTextColor.YELLOW));
        
        if (source.hasPermission("flashytitles.admin")) {
            source.sendMessage(Component.text("=== 管理员命令 ===", NamedTextColor.RED));
            source.sendMessage(Component.text("/title create <ID> <文本> <价格> [动画] [权限] [描述]", NamedTextColor.GRAY));
            source.sendMessage(Component.text("/title delete <ID> - 删除称号", NamedTextColor.GRAY));
            source.sendMessage(Component.text("/title give <玩家> <ID> - 给予称号", NamedTextColor.GRAY));
            source.sendMessage(Component.text("/title revoke <玩家> <ID> - 收回称号", NamedTextColor.GRAY));
            source.sendMessage(Component.text("/title coins add/set <玩家> <数量>", NamedTextColor.GRAY));
        }
    }
}
