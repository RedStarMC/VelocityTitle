# VelocityTitle



一个现代化的 Minecraft 跨平台称号系统，支持 Velocity、Spigot、Fabric 和 NeoForge，具有完整的占位符API支持和H2嵌入式数据库。

## 技术特性

### 核心技术栈
- **Java 21** - 现代Java特性支持
- **Gradle 8.6** - 构建系统
- **HikariCP** - 高性能数据库连接池
- **H2 Database** - 嵌入式数据库（默认）
- **MySQL/SQLite** - 可选数据库支持
- **Jedis** - Redis缓存支持

### 平台支持
- **Velocity** - 代理服务器支持，H2数据库
- **Spigot/Paper** - PlaceholderAPI集成
- **Fabric** - Text Placeholder API集成
- **NeoForge** - 内置占位符系统

### 占位符系统
- **Spigot**: 使用 PlaceholderAPI 标准扩展
- **Fabric**: 使用 Text Placeholder API 2.4.1+1.21
- **NeoForge**: 内置占位符服务，无需外部依赖

## 📦 安装与配置

### 快速安装

#### Velocity 服务器
```bash
# 1. 下载插件
wget velocity-1.0.0.jar

# 2. 放入plugins目录
cp velocity-1.0.0.jar velocity/plugins/

# 3. 重启服务器
# H2数据库会自动创建，无需额外配置
```

#### Spigot/Paper 服务器
```bash
# 1. 安装PlaceholderAPI (必需)
wget https://github.com/PlaceholderAPI/PlaceholderAPI/releases/latest/download/PlaceholderAPI.jar

# 2. 安装FlashyTitles
cp spigot-1.0.0.jar spigot/plugins/
cp PlaceholderAPI.jar spigot/plugins/

# 3. 重启服务器
```

#### Fabric 服务器
```bash
# 1. 确保安装Fabric API 0.105.0+1.21.1
# 2. 安装Text Placeholder API 2.4.1+1.21
# 3. 安装FlashyTitles
cp fabric-1.0.0.jar fabric/mods/
```

#### NeoForge 服务器
```bash
# 无需额外依赖，内置占位符系统
cp neoforge-1.0.0.jar neoforge/mods/
```


## 🎮 使用说明

### 玩家命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/titles` | 打开称号GUI菜单 | `flashytitles.use` |
| `/titles list` | 列出所有可用称号 | `flashytitles.use` |
| `/titles equip <称号ID>` | 装备指定称号 | `flashytitles.use` |
| `/titles unequip` | 取消装备当前称号 | `flashytitles.use` |
| `/titles preview <称号ID>` | 预览称号效果 | `flashytitles.use` |
| `/titles shop` | 打开称号商店 | `flashytitles.use` |
| `/titles buy <称号ID>` | 购买称号 | `flashytitles.use` |
| `/titles coins` | 查看金币余额 | `flashytitles.use` |

### 管理员命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/titleadmin reload` | 重载插件配置 | `flashytitles.admin` |
| `/titleadmin give <玩家> <称号ID>` | 给予玩家称号 | `flashytitles.admin` |
| `/titleadmin take <玩家> <称号ID>` | 移除玩家称号 | `flashytitles.admin` |
| `/titleadmin create <称号ID> <显示文本>` | 创建新称号 | `flashytitles.admin` |
| `/titleadmin delete <称号ID>` | 删除称号 | `flashytitles.admin` |
| `/titleadmin list` | 列出所有称号 | `flashytitles.admin` |
| `/titleadmin info <称号ID>` | 查看称号详细信息 | `flashytitles.admin` |
| `/titleadmin setprice <称号ID> <价格>` | 设置称号价格 | `flashytitles.admin` |
| `/titleadmin coins add <玩家> <数量>` | 给玩家添加金币 | `flashytitles.admin` |
| `/titleadmin coins set <玩家> <数量>` | 设置玩家金币 | `flashytitles.admin` |
| `/titleadmin coins remove <玩家> <数量>` | 移除玩家金币 | `flashytitles.admin` |

### 命令使用示例

#### 玩家使用流程
```bash
# 查看所有可用称号
/titles list

# 打开称号商店
/titles shop

# 购买称号（需要足够金币）
/titles buy vip

# 装备称号
/titles equip vip

# 预览称号效果
/titles preview rainbow

# 查看金币余额
/titles coins

# 取消装备称号
/titles unequip
```

#### 管理员管理流程
```bash
# 创建新称号
/titleadmin create vip "&6[VIP]" 1000 false "flashytitles.vip" "VIP专属称号"

# 给予玩家称号
/titleadmin give Steve vip

# 设置称号价格
/titleadmin setprice vip 2000

# 给玩家添加金币
/titleadmin coins add Steve 5000

# 查看称号信息
/titleadmin info vip

# 删除称号
/titleadmin delete old_title

# 重载配置
/titleadmin reload
```

## 🏷️ 占位符API详解

### Spigot - PlaceholderAPI 扩展
```java
// 在其他插件中使用
String title = PlaceholderAPI.setPlaceholders(player, "%flashytitles_title%");
```

**可用占位符：**
- `%flashytitles_title%` - 玩家当前称号（带颜色）
- `%flashytitles_title_raw%` - 原始称号文本（无颜色）
- `%flashytitles_title_id%` - 称号ID
- `%flashytitles_has_title%` - 是否有称号 (true/false)
- `%flashytitles_title_with_space%` - 称号+空格（如果有称号）
- `%flashytitles_title_prefix%` - 称号作为前缀使用

### Fabric - Text Placeholder API
```java
// 在Fabric模组中使用
Text titleText = Placeholders.parseText(
    Text.literal("%flashytitles:title%"),
    PlaceholderContext.of(player)
);
```

**可用占位符：**
- `%flashytitles:title%` - 玩家当前称号
- `%flashytitles:title_raw%` - 原始称号文本
- `%flashytitles:title_id%` - 称号ID
- `%flashytitles:has_title%` - 是否有称号
- `%flashytitles:title_with_space%` - 称号+空格
- `%flashytitles:title_prefix%` - 称号前缀

### NeoForge - 内置占位符系统
```java
// 在NeoForge模组中使用
PlaceholderService service = FlashyTitlesNeoForge.getPlaceholderService();
String title = service.getPlaceholderValue(playerUuid, "flashytitles_title");

// 或者处理包含占位符的文本
String processed = service.processPlaceholders(playerUuid, "Hello %flashytitles_title%!");
```

**可用占位符：**
- `%flashytitles_title%` - 玩家当前称号
- `%flashytitles_title_raw%` - 原始称号文本
- `%flashytitles_title_id%` - 称号ID
- `%flashytitles_has_title%` - 是否有称号
- `%flashytitles_title_with_space%` - 称号+空格
- `%flashytitles_title_prefix%` - 称号前缀

### 占位符使用示例

#### 在聊天格式中使用（Spigot）
```yaml
# EssentialsChat 配置
format: '%flashytitles_title_with_space%{DISPLAYNAME}: {MESSAGE}'

# ChatEx 配置
chat-format: '%flashytitles_title_with_space%%player_name%: %message%'
```

#### 在计分板中使用（Spigot）
```java
// 在计分板插件中
String prefix = PlaceholderAPI.setPlaceholders(player, "%flashytitles_title_prefix%");
scoreboard.getTeam(player.getName()).setPrefix(prefix);
```

#### 在TAB列表中使用（Spigot）
```yaml
# TAB插件配置
tablist-name: '%flashytitles_title_with_space%%player_name%'
```

## ⚙️ 高级配置

### 称号配置示例
```yaml
titles:
  vip:
    text: "&6[VIP]"
    price: 1000
    animated: false
    permission: "flashytitles.vip"
    description: "VIP专属称号"

  rainbow:
    text: "&c[&6R&ea&ai&bn&9b&do&5w&c]"
    price: 5000
    animated: true
    permission: "flashytitles.rainbow"
    description: "彩虹动画称号"

  admin:
    text: "&4[ADMIN]"
    price: 0
    animated: false
    permission: "flashytitles.admin"
    description: "管理员称号"
```

### 动画称号配置
```yaml
animations:
  rainbow:
    frames:
      - "&c[RAINBOW]"
      - "&6[RAINBOW]"
      - "&e[RAINBOW]"
      - "&a[RAINBOW]"
      - "&b[RAINBOW]"
      - "&9[RAINBOW]"
      - "&d[RAINBOW]"
    speed: 10  # ticks per frame
```

### 权限系统
```yaml
permissions:
  # 基础权限
  flashytitles.use: true  # 使用基本功能
  flashytitles.admin: false  # 管理员权限

  # 称号权限
  flashytitles.title.vip: false  # VIP称号权限
  flashytitles.title.rainbow: false  # 彩虹称号权限
  flashytitles.title.admin: false  # 管理员称号权限

  # 高级权限
  flashytitles.bypass.cost: false  # 绕过购买费用
  flashytitles.unlimited: false  # 无限制使用
```

### Redis配置（可选）
```yaml
redis:
  enabled: false  # 是否启用Redis
  host: "localhost"
  port: 6379
  password: ""
  database: 0
  timeout: 2000
```

## 开发信息

### 构建项目
```bash
# 克隆项目
git clone <repository-url>
cd velocity-flashy-titles

# 设置Java 21环境
export JAVA_HOME=/path/to/java21

# 构建所有模块
./gradlew build

# 构建特定模块
./gradlew :velocity:build
./gradlew :spigot:build
./gradlew :fabric:build
./gradlew :neoforge:build
```

### 项目结构
```
velocity-flashy-titles/
├── core/                 # 核心功能模块
├── velocity/             # Velocity平台实现
├── spigot/              # Spigot/Paper平台实现
├── fabric/              # Fabric平台实现
├── neoforge/            # NeoForge平台实现
├── build/jars/          # 构建产物
└── README.md
```

### API使用示例

#### Velocity插件集成
```java
// 获取FlashyTitles API
FlashyTitlesVelocity plugin = FlashyTitlesVelocity.getInstance();
TitleManager titleManager = plugin.getTitleManager();

// 给予玩家称号
titleManager.grantTitle(playerUuid, "vip");

// 装备称号
titleManager.equipTitle(playerUuid, "vip");
```

#### Spigot插件集成
```java
// 使用PlaceholderAPI
String title = PlaceholderAPI.setPlaceholders(player, "%flashytitles_title%");

// 在聊天格式中使用
String chatFormat = "%flashytitles_title_with_space%%player_name%: %message%";
```

## 📋 版本兼容性

| 平台 | 最低版本 | 推荐版本 | 依赖 |
|------|----------|----------|------|
| Velocity | 3.3.0 | 3.3.0+ | 无 |
| Spigot/Paper | 1.21.1 | 1.21.1+ | PlaceholderAPI |
| Fabric | 1.21.1 | 1.21.1+ | Fabric API, Text Placeholder API |
| NeoForge | 1.21.1 | 1.21.1+ | 无 |

### 依赖版本
- **Fabric API**: 0.105.0+1.21.1
- **Text Placeholder API**: 2.4.1+1.21
- **PlaceholderAPI**: 2.11.5+
- **Java**: 21+


### 常见问题

**Q: H2数据库文件在哪里？**
A: 默认在 `./data/flashytitles.mv.db`，可以通过配置文件修改路径。

**Q: 如何备份H2数据库？**
A: 直接复制 `./data/flashytitles.mv.db` 文件即可。

**Q: Redis是必需的吗？**
A: **不是必需的**！Redis是完全可选的，主要用于多服务器环境的实时同步优化。单服务器或小型群组服务器完全可以不配置Redis，插件会通过H2数据库正常工作。

**Q: 不配置Redis会影响功能吗？**
A: 不会影响基本功能。不配置Redis时：
- ✅ 所有称号功能正常工作
- ✅ 数据通过H2数据库存储和同步
- ✅ 单服务器环境完全没问题
- ⚠️ 多服务器环境同步可能稍慢（通过数据库同步而非实时同步）

**Q: PlaceholderAPI占位符不工作？**
A: 确保安装了PlaceholderAPI插件，并且FlashyTitles扩展已注册。使用 `/papi list` 检查。

**Q: Fabric占位符不显示？**
A: 确保安装了Text Placeholder API 2.4.1+1.21版本。

**Q: 如何从MySQL迁移到H2？**
A: 使用数据导出工具或联系开发者获取迁移脚本。

**Q: 称号不同步怎么办？**
A: 检查网络连接，确保所有服务器都安装了对应的插件/模组。

**Q: 动画称号不显示？**
A: 确保称号配置中 `animated: true`，并且客户端支持颜色代码。

### 日志调试
```yaml
# 在config.yml中启用调试模式
debug: true
log-level: "DEBUG"
```

### 性能优化
```yaml
# 数据库连接池配置
database:
  pool:
    maximum-pool-size: 10
    minimum-idle: 5
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000

# 缓存配置
cache:
  title-cache-size: 1000
  player-cache-size: 500
  cache-expire-minutes: 30

# Redis配置（可选）
redis:
  enabled: false  # 设为true启用Redis
  host: "localhost"
  port: 6379
  password: ""
  database: 0
```

## 📊 构建产物

### 已生成的JAR文件
- `build/jars/core-1.0.0.jar` - Core模块
- `build/jars/velocity-1.0.0.jar` - Velocity模块（H2数据库）
- `build/jars/spigot-1.0.0.jar` - Spigot模块（PlaceholderAPI）
- `build/jars/fabric-1.0.0.jar` - Fabric模块（Text Placeholder API）
- `build/jars/neoforge-1.0.0.jar` - NeoForge模块（内置占位符）
- `build/jars/FlashyTitles-Complete-1.0.0.jar` - 完整版本


## 快速开始

### 1分钟快速部署

#### Velocity群组服务器
```bash
# 1. 下载并安装Velocity端
wget build/jars/velocity-1.0.0.jar
cp velocity-1.0.0.jar velocity/plugins/

# 2. 下载并安装Spigot子服务器端
cp build/jars/spigot-1.0.0.jar spigot-server1/plugins/
cp build/jars/spigot-1.0.0.jar spigot-server2/plugins/

# 3. 重启所有服务器
# H2数据库会自动创建，无需额外配置
```

#### 单服务器部署
```bash
# Spigot服务器
cp build/jars/spigot-1.0.0.jar plugins/

# Fabric服务器
cp build/jars/fabric-1.0.0.jar mods/

# NeoForge服务器
cp build/jars/neoforge-1.0.0.jar mods/
```

### 首次使用
```bash
# 1. 启动服务器，插件会自动创建配置文件
# 2. 创建第一个称号
/titleadmin create vip "&6[VIP]" 1000 false "flashytitles.vip" "VIP专属称号"

# 3. 给玩家添加金币
/titleadmin coins add <玩家名> 5000

# 4. 玩家购买和装备称号
/titles shop
/titles buy vip
/titles equip vip
```
