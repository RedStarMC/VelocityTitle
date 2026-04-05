***如果你有任何疑问，或提交bug: 请发送[Issues](https://github.com/redstarmc/velocitytitle/issues)***

![License](https://img.shields.io/github/license/redstarmc/velocitytitle)
![Commit activity](https://img.shields.io/github/commit-activity/m/redstarmc/VelocityTitle)
![Repo size](https://img.shields.io/github/repo-size/redstarmc/velocitytitle)

# VelocityTitle [English](./docs/README_EN.md)

**VelocityTitle** 是一个适用于 Minecraft Velocity 群组服的称号插件。

它在 Velocity 服务端创建数据库存储玩家称号，并通过 Velocity 和子服的消息通道来发送数据。最终通过 PAPI 和 GUI(计划中)
在子服进行显示和操作。  
主要用于填补没有开源免费的跨服端插件的问题。

非常欢迎 PR！

## 下载 | Download

在其他平台发布之前，请到 [Releases](https://github.com/redstarmc/velocitytitle/releases) 处下载最新版本。

## 安装 | Install

1. 下载插件  
   你可以使用统一的插件，也可以分别下载不同平台的插件使用。但是 core 并不能作为插件使用。
2. 安装  
   你需要在服务端里正常安装插件。 注意 Velocity 必须安装，否则无法运行。
3. 配置插件
4. 设置 PAPI
   `velocitytitle_prefix` 和 `velocitytitle_suffix`

## 命令 & 权限 | Command & perms

### Velocity 命令 | Command

根命令 `/velocitytitle` & `/vt`

> [!tip]
> `<>` 为必填， `[]` 为选填， `()` 表示选择

* `player` 操作玩家
    - `divide` `<name>` `[player]` 向指定的玩家分配一个称号，不写玩家默认是自己
    - `list` `[player]` 列出指定的玩家的全部可用称号，不写玩家默认是自己
    - `pick` `( prefix | suffix | all)` 摘除指定的玩家的某个类型的称号
    - `revoke` `<name>` `<player>` 不再向指定的玩家分配这个称号
    - `wear` `<name>` `[player]` 为指定的玩家穿戴某个特定的称号，不写玩家默认是自己
* `title`
    - `create` `( prefix | suffix )` `<name>` `<display>` `[description]` 不填写描述默认是 无
    - `delete` `<name>` 删除指定的称号
    - `edit` `<name>` `( display | description )` `<data>`
    - `list` 列出全部称号
    - `meta` `<name>` 列出指定称号的信息
* `reload` 不稳定，暂时不要使用
* `help` 展示帮助列表
* `confirm` 执行等待确认的命令（仅在启用命令确认时可用）
* `cancel` 取消等待确认的命令（仅在启用命令确认时可用）

> [!important]
> 称号的 `name` 务必使用英文字母和下划线  
> 称号的 `display` 使用英文双引号即可输入中文

### Velocity 权限 | perms

`velocitytitle.admin` 管理员权限 包括了所有管理员应该有的权限

玩家需要使用的命令不需要设置权限

## 配置 | Configuration

配置文件均有详细注释，此处不再赘述

## 开发计划 | Develop Plan

第一阶段目标：

* [ ] 列表查询优化
* [ ] 支持 Miniplaceholder
* [ ] 优化命令分组
* [ ] 支持 Folia

第二阶段目标：

* 配置文件热重载
* 支持 MySQL
* 支持模组服务端

第三阶段目标：

* 支持 Redis 内存数据库模式
* 添加 GUI
* 数据库操作命令

<details>

<summary>折叠文本</summary>

* [x] Core 部分
    - [x] 文件操作
    - [x] 日志操作
* [ ] Velocity 部分
    - [x] 命令模块
        * [x] 根命令
        * [x] 重载配置(第二阶段计划)
      * [x] 命令帮助
        * [x] 称号操作
            - [x] 命令帮助
            - [x] 创建
            - [x] 删除
          - [x] 编辑
          - [x] 查看称号库
            - [x] 查看一个称号的信息
        * [ ] 数据库操作(可选，只能由控制台执行 3)
            - [ ] 文件备份
            - [ ] 导出数据
            - [ ] 执行数据库语句
            - [ ] 命令帮助
        * [x] 玩家操作
            - [x] 分配
            - [x] 收回
          - [x] 命令帮助
            - [x] 玩家穿戴
            - [x] 玩家取消穿戴
            - [x] 玩家查看自己的称号库
    - [x] 配置模块
        * [x] 插件配置
        * [x] 语言配置
        * [x] 配置读取和保存器
    - [x] 数据库模块
        * [x] EasySQL
        * [x] H2 数据库
        * [ ] MySQL 数据库(2)
        * [x] 数据库操作(和命令模块对接)
    - [x] 和其他部分进行数据通信
    - [ ] 其他
* [ ] Spigot 部分
    - [x] 和 Velocity 部分进行数据通信
    - [ ] 命令模块
        * [ ] 重载配置 (第二阶段计划)
        * [x] 命令帮助
        * [x] 根命令
    - [ ] GUI (可选 3) 模块
    - [x] PAPI或其他显示方法
    - [ ] 其他
* [ ] Fabric 同 Spigot(3)
* [ ] NeoForge 同 Spigot(3)

</details>


