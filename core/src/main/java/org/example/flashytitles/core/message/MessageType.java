package org.example.flashytitles.core.message;

/**
 * 消息类型枚举
 * 用于 Velocity 和 Spigot 之间的通信
 */
public enum MessageType {
    // 称号相关
    TITLE_UPDATE("title_update"),           // 更新玩家称号
    TITLE_REMOVE("title_remove"),           // 移除玩家称号
    TITLE_SYNC("title_sync"),               // 同步称号数据
    
    // 玩家数据相关
    PLAYER_JOIN("player_join"),             // 玩家加入服务器
    PLAYER_QUIT("player_quit"),             // 玩家离开服务器
    PLAYER_DATA_REQUEST("player_data_req"), // 请求玩家数据
    PLAYER_DATA_RESPONSE("player_data_res"), // 响应玩家数据
    
    // 权限相关
    PERMISSION_CHECK("permission_check"),   // 权限检查请求
    PERMISSION_RESPONSE("permission_res"),  // 权限检查响应

    // 系统相关
    RELOAD_CONFIG("reload_config"),         // 重载配置
    SYNC_ALL("sync_all"),                   // 同步所有数据
    HEARTBEAT("heartbeat");                 // 心跳包
    
    private final String id;
    
    MessageType(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public static MessageType fromId(String id) {
        for (MessageType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
