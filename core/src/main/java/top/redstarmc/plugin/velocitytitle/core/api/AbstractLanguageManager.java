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

import com.moandjiezana.toml.Toml;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>多语言管理器抽象类</b><br>
 * 从 {@code lang/} 目录加载 TOML 格式的语言文件，<br>
 * 并通过实现 {@link Translator} 接口注册到 Adventure 的 {@link GlobalTranslator}。<br>
 * <br>
 * 子类只需实现 {@link #builtInLangFiles()} 指定内置语言文件即可。
 */
public abstract class AbstractLanguageManager implements Manager, Translator {

    /** 默认回退语言，固定为简体中文 */
    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;
    private final File langDir;
    private final Key name;
    /** 存储所有已加载语言的翻译 */
    private final Map<Locale, Map<String, MessageFormat>> translations = new ConcurrentHashMap<>();
    private Toml currentToml;

    public AbstractLanguageManager(File dataFolder) {
        this.langDir = new File(dataFolder, "lang");
        this.name = Key.key("velocitytitle", "translations");
    }

    protected abstract String[] builtInLangFiles();

    /**
     * 初始化：创建目录 → 释放默认文件 → 加载选中语言
     *
     * @param langCode 语言代码，如 "zh_CN"
     */
    public void init(String langCode) {
        if ( ! langDir.exists() ) {
            langDir.mkdirs();
        }
        for ( String fileName : builtInLangFiles() ) {
            extractDefault(fileName);
        }
        load(langCode);
    }

    /**
     * 加载 / 重载指定语言<br>
     * 若找不到对应文件，回退到第一个内置语言。
     *
     * @param langCode 语言代码
     */
    public void load(String langCode) {
        File target = new File(langDir, langCode + ".toml");

        if ( ! target.exists() ) {
            String[] builtIn = builtInLangFiles();
            if ( builtIn.length == 0 ) {
                throw new IllegalStateException("No built-in lang files defined");
            }
            String fallback = builtIn[0].replace(".toml", "");
            System.out.println("[VelocityTitle] Lang file not found: " + langCode
                    + ".toml, falling back to " + fallback);
            langCode = fallback;
            target = new File(langDir, langCode + ".toml");
        }

        // 读取当前选中语言
        currentToml = new Toml().read(target);

        // 重新注册翻译
        registerAll();
    }

    /**
     * 卸载翻译（插件关闭时调用）
     */
    public void close() {
        GlobalTranslator.translator().removeSource(this);
        translations.clear();
    }

    @Override
    public @NotNull Key name() {
        return name;
    }

    /**
     * Adventure 调用此方法来获取翻译<br>
     * 查找顺序：精确匹配 → 默认语言
     */
    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        // 精确匹配
        Map<String, MessageFormat> localeMap = translations.get(locale);
        if ( localeMap != null ) {
            MessageFormat format = localeMap.get(key);
            if ( format != null ) return format;
        }

        // 回退到默认语言
        if ( ! locale.equals(DEFAULT_LOCALE) ) {
            Map<String, MessageFormat> defaultMap = translations.get(DEFAULT_LOCALE);
            if ( defaultMap != null ) {
                return defaultMap.get(key);
            }
        }

        // 无语言
        return null;
    }

    /**
     * 获得当前已加载的语言文件
     *
     * @return {@link Toml} 格式的配置文件
     */
    public final Toml getConfigToml() {
        return currentToml;
    }


    // 操作

    /**
     * 扫描 {@code lang/} 下所有 {@code .toml}，加载并注册到 {@link GlobalTranslator}
     */
    private void registerAll() {
        GlobalTranslator.translator().removeSource(this);
        translations.clear();

        File[] langFiles = langDir.listFiles((dir, fileName) -> fileName.endsWith(".toml"));
        if ( langFiles == null ) return;

        for ( File langFile : langFiles ) {
            String code = langFile.getName().replace(".toml", "");
            Locale locale = parseLocale(code);

            Toml toml = new Toml().read(langFile);
            Map<String, MessageFormat> map = new ConcurrentHashMap<>();
            flatten("", toml.toMap(), map);

            translations.put(locale, map);
        }

        GlobalTranslator.translator().addSource(this);
    }

    @SuppressWarnings("unchecked")
    private void flatten(String prefix, Map<String, Object> map, Map<String, MessageFormat> result) {
        for ( Map.Entry<String, Object> entry : map.entrySet() ) {
            String fullKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if ( entry.getValue() instanceof Map ) {
                flatten(fullKey, (Map<String, Object>) entry.getValue(), result);
            } else if ( entry.getValue() instanceof String ) {
                String value = ((String) entry.getValue()).replace("'", "''");
                value = autoNumberPlaceholders(value);
                result.put(fullKey, new MessageFormat(value));
            } else {
                result.put(fullKey, new MessageFormat(String.valueOf(entry.getValue())));
            }
        }
    }

    /**
     * 将 {@code "zh_CN"} 解析为 {@link Locale}
     */
    private Locale parseLocale(String localeCode) {
        String[] parts = localeCode.split("_");
        return parts.length >= 2
                ? Locale.of(parts[0], parts[1])
                : Locale.of(parts[0]);
    }

    /**
     * 从 jar 资源释放默认语言文件到 {@code lang/} 目录（不覆盖已存在文件）
     */
    private void extractDefault(String fileName) {
        File target = new File(langDir, fileName);
        if ( target.exists() ) return;

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("lang/" + fileName)) {
            if ( in == null ) return;
            Files.copy(in, target.toPath());
        } catch (IOException e) {
            System.out.println("[VelocityTitle] Failed to extract lang file: " + fileName);
        }
    }

    private String autoNumberPlaceholders(String value) {
        int index = 0;
        StringBuilder sb = new StringBuilder(value.length());
        for ( int i = 0; i < value.length(); i++ ) {
            if ( i + 1 < value.length() && value.charAt(i) == '{' && value.charAt(i + 1) == '}' ) {
                sb.append('{').append(index++).append('}');
                i++; // 跳过 '}'
            } else {
                sb.append(value.charAt(i));
            }
        }
        return sb.toString();
    }

}
