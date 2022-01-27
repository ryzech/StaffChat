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
