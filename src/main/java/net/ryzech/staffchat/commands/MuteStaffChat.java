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

package net.ryzech.staffchat.commands;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.ryzech.staffchat.StaffChat;
import net.ryzech.staffchat.utils.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteStaffChat implements SimpleCommand
{
    public static List<UUID> mutedStaffList = new ArrayList<>();
    public Toml config = StaffChat.getInstance().getConfig();

    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (!(source instanceof Player)) return;
        Player player = (Player) source;
        if (!player.hasPermission(Permissions.STAFFCHAT_USE)) {
            player.sendMessage(MiniMessage.get().deserialize(config.getString("messages.no-permission")));
            return;
        }
        if (mutedStaffList.contains(player.getUniqueId())) {
            mutedStaffList.remove(player.getUniqueId());
            player.sendMessage(MiniMessage.get().deserialize(config.getString("messages.mute-off")));
        } else {
            mutedStaffList.add(player.getUniqueId());
            player.sendMessage(MiniMessage.get().deserialize(config.getString("messages.mute-on")));
        }
    }
}
