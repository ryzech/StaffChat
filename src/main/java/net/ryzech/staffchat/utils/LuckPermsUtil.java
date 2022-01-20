package net.ryzech.staffchat.utils;

import com.velocitypowered.api.proxy.Player;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.ryzech.staffchat.StaffChat;

public class LuckPermsUtil {
    public static String getPrefix(Player player) {
        LuckPerms luckPerms = StaffChat.getInstance().getLuckPermsProvider();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user.getCachedData().getMetaData().getPrefix() == null)
            return "";
        return user.getCachedData().getMetaData().getPrefix();
    }
}
