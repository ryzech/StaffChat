package net.ryzech.staffchat.commands;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.ryzech.staffchat.StaffChat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToggleStaffChat implements SimpleCommand {
    public static List<UUID> toggleStaffList = new ArrayList<>();
    public Toml config = StaffChat.getInstance().getConfig();

    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (!config.getBoolean("toggle.enabled")) return;
        if (!(source instanceof Player)) return;
        Player player = (Player) source;
        if (!player.hasPermission("staffchat.toggle")) {
            player.sendMessage(MiniMessage.get().deserialize(config.getString("messages.no-permission")));
            return;
        }
        if (toggleStaffList.contains(player.getUniqueId())) {
            toggleStaffList.remove(player.getUniqueId());
            player.sendMessage(MiniMessage.get().deserialize(config.getString("messages.toggle-off")));
        } else {
            toggleStaffList.add(player.getUniqueId());
            player.sendMessage(MiniMessage.get().deserialize(config.getString("messages.toggle-on")));
        }
    }
}
