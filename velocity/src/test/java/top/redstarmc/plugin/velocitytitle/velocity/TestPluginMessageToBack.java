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

package top.redstarmc.plugin.velocitytitle.velocity;

import com.velocitypowered.api.proxy.Player;
import top.redstarmc.plugin.velocitytitle.core.util.NetWorkReader;

import java.io.IOException;

public class TestPluginMessageToBack {

    public static void test(Player player){

        String[] d = {"UpdateTitle","uuid-----------------","title_type","title_display","others"};

        try {
            byte[][] data = NetWorkReader.buildMessage(d);



        } catch (IOException e) {
            VelocityTitleVelocity.getInstance().getLogger().crash(e, "≤‚ ‘ ß∞‹");
        }

    }

}
