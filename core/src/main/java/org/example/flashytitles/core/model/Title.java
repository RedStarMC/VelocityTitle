package org.example.flashytitles.core.model;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * 称号模型类
 * 支持动态效果和颜色变化
 */
public class Title {
    private final String id;
    private final String raw;
    private final int price;
    private final boolean animated;
    private final String permission;
    private final String description;
    
    public Title(String id, String raw, int price) {
        this(id, raw, price, false, null, "");
    }
    
    public Title(String id, String raw, int price, boolean animated, String permission, String description) {
        this.id = id;
        this.raw = raw;
        this.price = price;
        this.animated = animated;
        this.permission = permission;
        this.description = description != null ? description : "";
    }
    
    /**
     * 渲染称号文本，支持动态效果
     * @param tick 当前tick值，用于动画计算
     * @return 渲染后的文本
     */
    public String render(int tick) {
        if (!animated) {
            return raw;
        }
        
        // 动态效果实现
        return AnimationUtil.renderAnimatedText(raw, tick);
    }
    
    /**
     * 获取静态显示文本（用于GUI等场景）
     */
    public String getDisplayText() {
        return AnimationUtil.getStaticDisplay(raw);
    }
    
    /**
     * 转换为JSON对象
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("raw", raw);
        json.addProperty("price", price);
        json.addProperty("animated", animated);
        if (permission != null) {
            json.addProperty("permission", permission);
        }
        json.addProperty("description", description);
        return json;
    }
    
    /**
     * 从JSON对象创建称号
     */
    public static Title fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        String raw = json.get("raw").getAsString();
        int price = json.get("price").getAsInt();
        boolean animated = json.has("animated") ? json.get("animated").getAsBoolean() : false;
        String permission = json.has("permission") ? json.get("permission").getAsString() : null;
        String description = json.has("description") ? json.get("description").getAsString() : "";
        
        return new Title(id, raw, price, animated, permission, description);
    }
    
    // Getters
    public String getId() { return id; }
    public String getRaw() { return raw; }
    public int getPrice() { return price; }
    public boolean isAnimated() { return animated; }
    public String getPermission() { return permission; }
    public String getDescription() { return description; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Title title = (Title) o;
        return Objects.equals(id, title.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Title{" +
                "id='" + id + '\'' +
                ", raw='" + raw + '\'' +
                ", price=" + price +
                ", animated=" + animated +
                '}';
    }
}
