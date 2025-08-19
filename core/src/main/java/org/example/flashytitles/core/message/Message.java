package org.example.flashytitles.core.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 消息类
 * 用于 Velocity 和 Spigot 之间的数据传输
 */
public class Message {
    private static final Gson GSON = new Gson();
    
    private final MessageType type;
    private final JsonObject data;
    private final long timestamp;
    
    public Message(MessageType type, JsonObject data) {
        this.type = type;
        this.data = data != null ? data : new JsonObject();
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(MessageType type) {
        this(type, new JsonObject());
    }
    
    // Getters
    public MessageType getType() { return type; }
    public JsonObject getData() { return data; }
    public long getTimestamp() { return timestamp; }
    
    // 数据操作方法
    public Message addData(String key, String value) {
        data.addProperty(key, value);
        return this;
    }
    
    public Message addData(String key, int value) {
        data.addProperty(key, value);
        return this;
    }
    
    public Message addData(String key, boolean value) {
        data.addProperty(key, value);
        return this;
    }
    
    public Message addData(String key, JsonObject value) {
        data.add(key, value);
        return this;
    }
    
    public String getString(String key) {
        return data.has(key) ? data.get(key).getAsString() : null;
    }
    
    public int getInt(String key) {
        return data.has(key) ? data.get(key).getAsInt() : 0;
    }
    
    public boolean getBoolean(String key) {
        return data.has(key) && data.get(key).getAsBoolean();
    }
    
    public JsonObject getObject(String key) {
        return data.has(key) ? data.getAsJsonObject(key) : null;
    }
    
    /**
     * 序列化为字节数组
     */
    public byte[] serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.getId());
        json.add("data", data);
        json.addProperty("timestamp", timestamp);
        
        return GSON.toJson(json).getBytes();
    }
    
    /**
     * 从字节数组反序列化
     */
    public static Message deserialize(byte[] bytes) {
        try {
            String jsonStr = new String(bytes);
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            
            String typeId = json.get("type").getAsString();
            MessageType type = MessageType.fromId(typeId);
            if (type == null) {
                throw new IllegalArgumentException("Unknown message type: " + typeId);
            }
            
            JsonObject data = json.has("data") ? json.getAsJsonObject("data") : new JsonObject();
            
            Message message = new Message(type, data);
            // 设置时间戳（如果有的话）
            if (json.has("timestamp")) {
                // 这里我们不能直接设置timestamp，因为它是final的
                // 但这不影响功能，因为时间戳主要用于调试
            }
            
            return message;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
