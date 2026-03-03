***如果你有任何疑问，或提交bug: 请发送[Issues](https://github.com/redstarmc/velocitytitle/issues)***

![License](https://img.shields.io/github/license/redstarmc/velocitytitle)
![Commit activity](https://img.shields.io/github/commit-activity/m/redstarmc/VelocityTitle)
![Repo size](https://img.shields.io/github/repo-size/redstarmc/velocitytitle)

# VelocityTitle

**VelocityTitle** 是一个适用于 Minecraft Velocity 群组服的称号插件。

它在 Velocity 服务端创建数据库存储玩家称号，并通过 Velocity 和子服的消息通道来发送数据。最终通过 PAPI 和 GUI(计划中)
在子服进行显示和操作。  
主要用于填补没有开源免费的跨服端插件的问题。

## 下载

在其他平台发布之前，请到 [Releases](https://github.com/redstarmc/velocitytitle/releases) 处下载最新版本。

## 命令 & 权限

### Velocity 端命令

根命令 `/velocitytitle` & `/vt`

* `player`
    - `divide`
    - `list`
    - `pick`
    - `revoke`
    - `wear`
* `title`
    - `create`
    - `delete`
    - `edit`
    - `list`
    - `meta`
* `reload` 不稳定，暂时不要使用

> [!important]
> 称号的 `name` 务必使用英文字母和下划线
> 称号的 `display` 使用英文双引号即可输入中文

## 配置

配置文件均有详细注释，此处不再赘述

## 开发计划

第一阶段目标：(进行中)

* [ ] 修复已知 bug
* [ ] 完善命令反馈和命令帮助
* [ ] 将前后缀改为枚举管理
* [ ] 完善权限管理
* [ ] 增加命令确认系统
* [ ] 列表查询优化

第二阶段目标：

* 配置文件热重载
* 支持 MySQL
* 添加 GUI

第三阶段目标：

* Redis 内存数据库
* 支持模组服务端
* 数据库操作命令

<details>

<summary>折叠文本</summary>

* [x] Core 部分
    - [x] 文件操作
    - [x] 日志操作
* [ ] Velocity 部分
    - [ ] 命令模块
        * [x] 根命令
        * [x] 重载配置(第二阶段计划)
      * [x] 命令帮助
        * [x] 称号操作
            - [x] 命令帮助
            - [x] 创建
            - [x] 删除
          - [x] 编辑
            - [ ] 查看称号库
            - [x] 查看一个称号的信息
        * [ ] 数据库操作(可选，只能由控制台执行 3)
            - [ ] 文件备份
            - [ ] 导出数据
            - [ ] 执行数据库语句
            - [ ] 命令帮助
        * [ ] 玩家操作
            - [x] 分配
            - [x] 收回
            - [ ] 命令帮助
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


