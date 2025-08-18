package org.example.flashytitles.spigot.model;

/**
 * 玩家称号数据类
 */
public class PlayerTitleData {
    private final String titleId;
    private final String rawText;
    private final boolean animated;
    
    public PlayerTitleData(String titleId, String rawText, boolean animated) {
        this.titleId = titleId;
        this.rawText = rawText;
        this.animated = animated;
    }
    
    public String getTitleId() { 
        return titleId; 
    }
    
    public String getRawText() { 
        return rawText; 
    }
    
    public boolean isAnimated() { 
        return animated; 
    }
    
    @Override
    public String toString() {
        return "PlayerTitleData{" +
                "titleId='" + titleId + '\'' +
                ", rawText='" + rawText + '\'' +
                ", animated=" + animated +
                '}';
    }
}
