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

public class toStrings {

    /**
     * 按照 SLF4J 风格将 {} 占位符替换为参数值。
     * <p>示例: format("Hello {}, you are {} years old", "Alice", 30)
     *       -> "Hello Alice, you are 30 years old"</p>
     *
     * @param format 含 {} 占位符的模板字符串
     * @param params 替换参数
     * @return 格式化后的字符串
     */
    public static String format(String format, Object... params) {
        if ( format == null || params == null || params.length == 0 ) {
            return format;
        }

        StringBuilder sb = new StringBuilder(format.length() + 64);
        int paramIdx = 0;
        int i = 0;

        while ( i < format.length() ) {
            if ( i + 1 < format.length() && format.charAt(i) == '{' && format.charAt(i + 1) == '}' ) {
                if ( paramIdx < params.length ) {
                    sb.append(params[paramIdx]);
                    paramIdx++;
                } else {
                    sb.append("{}");  // 参数不够时保留原始占位符
                }
                i += 2;
            } else {
                sb.append(format.charAt(i));
                i++;
            }
        }

        return sb.toString();
    }
}
