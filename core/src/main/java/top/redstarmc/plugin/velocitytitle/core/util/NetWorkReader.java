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

package top.redstarmc.plugin.velocitytitle.core.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 本工具类由 AI 生成
 * <br>
 * 跨服通信的消息读写工具类，负责字符串数组与字节数组的相互转换，
 * 支持网络传输中的消息序列化与反序列化，并标记消息解析状态。
 * <p>
 * 核心功能：
 * <ul>
 *   <li>将字符串数组（业务数据）序列化为字节数组（网络传输格式）</li>
 *   <li>将字节数组（网络接收数据）解析为字符串数组（供业务逻辑使用）</li>
 *   <li>通过 {@link #isCompleted()} 标记消息是否解析完成，确保数据完整性</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>
 * // 序列化：将字符串数组转为字节数组（发送时）
 * byte[][] data = NetWorkReader.buildMessage("BroadcastRaw", "player123", "Hello");
 *
 * // 反序列化：将接收的字节数组解析为字符串数组（接收时）
 * NetWorkReader reader = NetWorkReader.read(receivedBytes);
 * if (reader.isCompleted()) {
 *     String[] parts = reader.build();
 *     // 处理业务逻辑
 * }
 * </pre>
 */
public class NetWorkReader {
    private final List<String> parts = new ArrayList<>();
    private boolean completed = false;

    /**
     * 私有构造方法，禁止外部直接实例化，需通过 {@link #read(byte[])} 创建实例。
     */
    private NetWorkReader() {}

    /**
     * 将字节数组解析为 NetWorkReader 实例，提取其中的字符串数组。
     * 解析逻辑：先读取数组长度，再依次读取每个字符串（使用 UTF-8 编码）。
     *
     * @param data 待解析的字节数组（通常来自网络传输）
     * @return 包含解析结果的 NetWorkReader 实例
     * @throws IOException 若字节数组格式错误或读取失败（如长度不匹配）
     */
    public static NetWorkReader read(byte[] data) throws IOException {
        NetWorkReader reader = new NetWorkReader();
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
            int length = in.readInt(); // 读取字符串数组的长度
            for (int i = 0; i < length; i++) {
                reader.parts.add(in.readUTF()); // 依次读取每个字符串
            }
            reader.completed = true; // 标记解析完成
        }
        return reader;
    }

    /**
     * 将字符串数组序列化为字节数组，用于网络传输。
     * 序列化逻辑：先写入数组长度，再依次写入每个字符串（使用 UTF-8 编码）。
     *
     * @param parts 待序列化的字符串数组（第一个元素通常为消息类型，如 "BroadcastRaw"）
     * @return 包含序列化结果的字节数组（外层数组用于支持分片传输，当前实现为单个数组）
     * @throws IOException 若字符串序列化失败（如包含不支持的字符）
     */
    public static byte[][] buildMessage(@NotNull String... parts) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out)) {
            dataOut.writeInt(parts.length); // 写入数组长度
            for (String part : parts) {
                dataOut.writeUTF(part); // 依次写入每个字符串
            }
            return new byte[][]{out.toByteArray()}; // 外层数组预留分片扩展
        }
    }

    /**
     * 判断消息是否解析完成。
     *
     * @return 若解析完成（所有字符串均被正确读取）则返回 true，否则返回 false
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * 获取解析后的字符串数组。
     * 需先通过 {@link #isCompleted()} 确认解析完成，否则可能返回不完整数据。
     *
     * @return 解析后的字符串数组，若未完成解析则返回空数组
     */
    public String[] build() {
        return parts.toArray(new String[0]);
    }
}
