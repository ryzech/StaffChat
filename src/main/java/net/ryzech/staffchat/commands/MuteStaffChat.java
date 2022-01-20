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
