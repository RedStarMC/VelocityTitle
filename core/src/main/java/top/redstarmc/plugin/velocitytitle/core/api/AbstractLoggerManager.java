/*
 * This file is part of VelocityTitle(https://github.com/RedStarMC/VelocityTitle).
 *
 * Copyright (C) RedStarMC and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package top.redstarmc.plugin.velocitytitle.core.api;

import top.redstarmc.plugin.velocitytitle.core.util.toStrings;

/**
 * <b>日志管理器</b>
 * 负责向控制台输出日志。所有日志通过 {@link #sendMessage(String)} 输出，由子类实现来适配多平台。
 */
public abstract class AbstractLoggerManager {

    /** 日志前缀 */
    public String INFO_PREFIX;

    /** 是否启用 debug 模式 */
    public boolean debugMode;

    /**
     * 构造器
     *
     * @param INFO_PREFIX 主类传入日志前缀
     */
    public AbstractLoggerManager(String INFO_PREFIX) {
        this(INFO_PREFIX, false);
        //
    }

    /**
     * 构造器
     * @param INFO_PREFIX 主类传入日志前缀
     * @param debugMode 主类传入是否启用 debug 模式
     */
    public AbstractLoggerManager(String INFO_PREFIX, boolean debugMode) {
        this.INFO_PREFIX = INFO_PREFIX;
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
        //
    }

    /**
     * 向控制台打印的方法
     * @param msg 内容
     */
    public abstract void sendMessage(String msg);

    /**
     * 发送插件普通信息
     * @param messages 字符串
     */
    public void info(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            if (message == null) continue;
            sendMessage(INFO_PREFIX + "§a[INFO] §r" + message + "§r");
        }
    }

    /**
     * 发送插件格式化信息
     * @param messages 字符串
     * @param objects 传入的格式化内容
     */
    public void info(String messages, Object... objects) {
        if (messages == null) return;
        sendMessage(INFO_PREFIX + "§a[INFO] §r" + toStrings.format(messages,objects) + "§r");
    }

    /**
     * 发送插件警告信息
     * @param messages 字符串
     */
    public void warn(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            if (message == null) continue;
            sendMessage(INFO_PREFIX + "§e[WARN] §r" + message + "§r");
        }
    }

    /**
     * 发送插件错误信息
     * @param messages 字符串
     */
    public void error(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            if (message == null) continue;
            sendMessage(INFO_PREFIX + "§c[ERROR] §r" + message + "§r");
        }
    }

    /**
     * 发送插件debug信息
     * @param messages 字符串
     */
    public void debug(String... messages) {
        if (messages == null) return;
        if (isDebugMode()) {
            for (String message : messages) {
                if (message == null) continue;
                sendMessage(INFO_PREFIX + "§6[DEBUG] §r" + message + "§r");
            }
        }
    }

    /**
     * 发送插件debug堆栈
     * @param e 堆栈
     */
    public void debug(Throwable e) {
        if (e == null) return;
        if (isDebugMode())
            e.printStackTrace();
    }

    /**
     * 同时发送插件debug信息和堆栈
     * @param e 堆栈
     * @param msg 字符串
     */
    public void debug(String msg, Throwable e) {
        if (msg == null || e == null) return;
        if (isDebugMode()) {
            debug(msg);
            debug(e);
        }
    }

    /**
     * 发送插件 数据库 debug信息
     * @param messages 字符串
     * @param objects 参数
     */
    public void debugDataBase(String messages, Object... objects) {
        if (messages == null) return;
        if (isDebugMode()) {
            sendMessage(INFO_PREFIX + "§6[DEBUG DB] §r" + toStrings.format(messages,objects) + "§r");
        }
    }

    /**
     * 抛出错误堆栈和错误信息
     * @param throwable 堆栈
     * @param messages 信息
     */
    public void crash(Throwable throwable, String... messages){
        for (String message : messages) {
            if (message == null) continue;
            sendMessage(INFO_PREFIX + "§c[ERROR] §r" + message + "§r");
        }
        sendMessage(INFO_PREFIX + "§c[ERROR] §r" + "错误信息 ->" + "§r");
        sendMessage(INFO_PREFIX + "§c[ERROR] §r" + throwable.getMessage() + "§r");
        sendMessage(INFO_PREFIX + "§c[ERROR] §r" + "错误堆栈 ->" + "§r");
        throwable.printStackTrace();
    }

}
