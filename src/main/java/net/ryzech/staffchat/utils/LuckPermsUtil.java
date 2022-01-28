/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 RyzechDev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ryzech.staffchat.utils;

import com.velocitypowered.api.proxy.Player;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.ryzech.staffchat.StaffChat;

public class LuckPermsUtil {
    private static LuckPerms luckPerms = StaffChat.getInstance().getLuckPermsProvider();
    public static String getPrefix(Player player) 
    {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user.getCachedData().getMetaData().getPrefix() == null)
            return "";
        return user.getCachedData().getMetaData().getPrefix();
    }

    public static String getSuffix(Player player) 
    {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user.getCachedData().getMetaData().getSuffix() == null)
            return "";
        return user.getCachedData().getMetaData().getSuffix();
    }
}
