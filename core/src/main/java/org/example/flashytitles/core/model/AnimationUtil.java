package org.example.flashytitles.core.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动画工具类
 * 处理称号的动态效果
 */
public class AnimationUtil {
    
    // 颜色代码映射
    private static final String[] COLORS = {
        "§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
        "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"
    };
    
    // 彩虹色序列
    private static final String[] RAINBOW_COLORS = {
        "§c", "§6", "§e", "§a", "§b", "§9", "§d"
    };
    
    // 渐变色序列
    private static final String[] GRADIENT_COLORS = {
        "§c", "§6", "§e", "§f", "§e", "§6", "§c"
    };
    
    /**
     * 渲染动画文本
     * @param raw 原始文本
     * @param tick 当前tick值
     * @return 渲染后的文本
     */
    public static String renderAnimatedText(String raw, int tick) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }

        // 限制文本长度，防止性能问题
        if (raw.length() > 256) {
            raw = raw.substring(0, 256);
        }

        // 确保tick值在合理范围内，防止溢出
        tick = Math.abs(tick % 10000);

        try {
            // 检测动画类型
            if (raw.contains("{rainbow}")) {
                return renderRainbow(raw, tick);
            } else if (raw.contains("{gradient}")) {
                return renderGradient(raw, tick);
            } else if (raw.contains("{blink}")) {
                return renderBlink(raw, tick);
            } else if (raw.contains("{wave}")) {
                return renderWave(raw, tick);
            } else if (containsMultipleColors(raw)) {
                return renderColorCycle(raw, tick);
            }
        } catch (Exception e) {
            // 如果动画渲染失败，返回静态文本
            return getStaticDisplay(raw);
        }

        return raw;
    }
    
    /**
     * 获取静态显示文本（移除动画标记）
     */
    public static String getStaticDisplay(String raw) {
        if (raw == null) return "";
        
        return raw.replaceAll("\\{rainbow\\}", "")
                  .replaceAll("\\{gradient\\}", "")
                  .replaceAll("\\{blink\\}", "")
                  .replaceAll("\\{wave\\}", "");
    }
    
    /**
     * 彩虹效果
     */
    private static String renderRainbow(String raw, int tick) {
        String text = raw.replace("{rainbow}", "");
        StringBuilder result = new StringBuilder();

        // 移除现有颜色代码
        String cleanText = text.replaceAll("§[0-9a-fk-or]", "");

        // 确保有颜色数组
        if (RAINBOW_COLORS.length == 0) {
            return cleanText;
        }

        for (int i = 0; i < cleanText.length(); i++) {
            char c = cleanText.charAt(i);
            if (c != ' ') {
                // 安全的数组访问
                int colorIndex = Math.abs((tick / 2 + i)) % RAINBOW_COLORS.length;
                result.append(RAINBOW_COLORS[colorIndex]);
            }
            result.append(c);
        }

        return result.toString();
    }
    
    /**
     * 渐变效果
     */
    private static String renderGradient(String raw, int tick) {
        String text = raw.replace("{gradient}", "");
        StringBuilder result = new StringBuilder();
        
        String cleanText = text.replaceAll("§[0-9a-fk-or]", "");
        
        for (int i = 0; i < cleanText.length(); i++) {
            char c = cleanText.charAt(i);
            if (c != ' ') {
                int colorIndex = (tick / 3 + i) % GRADIENT_COLORS.length;
                result.append(GRADIENT_COLORS[colorIndex]);
            }
            result.append(c);
        }
        
        return result.toString();
    }
    
    /**
     * 闪烁效果
     */
    private static String renderBlink(String raw, int tick) {
        String text = raw.replace("{blink}", "");
        
        // 每20tick闪烁一次
        if ((tick / 10) % 2 == 0) {
            return "§f" + text;
        } else {
            return "§7" + text;
        }
    }
    
    /**
     * 波浪效果
     */
    private static String renderWave(String raw, int tick) {
        String text = raw.replace("{wave}", "");
        StringBuilder result = new StringBuilder();
        
        String cleanText = text.replaceAll("§[0-9a-fk-or]", "");
        
        for (int i = 0; i < cleanText.length(); i++) {
            char c = cleanText.charAt(i);
            if (c != ' ') {
                // 使用正弦波计算颜色
                double wave = Math.sin((tick + i * 2) * 0.1);
                if (wave > 0.5) {
                    result.append("§b");
                } else if (wave > 0) {
                    result.append("§3");
                } else if (wave > -0.5) {
                    result.append("§1");
                } else {
                    result.append("§9");
                }
            }
            result.append(c);
        }
        
        return result.toString();
    }
    
    /**
     * 颜色循环效果（检测到多个颜色代码时）
     */
    private static String renderColorCycle(String raw, int tick) {
        // 提取所有颜色代码
        Pattern pattern = Pattern.compile("§[0-9a-fk-or]");
        Matcher matcher = pattern.matcher(raw);
        
        StringBuilder colors = new StringBuilder();
        while (matcher.find()) {
            colors.append(matcher.group());
        }
        
        if (colors.length() < 4) { // 至少需要2个颜色代码
            return raw;
        }
        
        // 循环替换颜色
        String result = raw;
        int offset = (tick / 5) % (colors.length() / 2);
        
        for (int i = 0; i < colors.length(); i += 2) {
            if (i + 1 < colors.length()) {
                int newIndex = (i + offset * 2) % colors.length();
                if (newIndex + 1 < colors.length()) {
                    String oldColor = colors.substring(i, i + 2);
                    String newColor = colors.substring(newIndex, newIndex + 2);
                    result = result.replaceFirst(Pattern.quote(oldColor), newColor);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 检测是否包含多个颜色代码
     */
    private static boolean containsMultipleColors(String text) {
        Pattern pattern = Pattern.compile("§[0-9a-fk-or]");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
            if (count >= 2) return true;
        }
        return false;
    }
}
