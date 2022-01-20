package net.ryzech.staffchat.listeners;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.ryzech.staffchat.StaffChat;
import net.ryzech.staffchat.commands.ToggleStaffChat;
import net.ryzech.staffchat.utils.LuckPermsUtil;
import net.ryzech.staffchat.utils.Permissions;

import static net.ryzech.staffchat.commands.MuteStaffChat.mutedStaffList;

public class ChatListener extends ListenerAdapter {
    public Toml config = StaffChat.getInstance().getConfig();

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event)
    {
        Player player = event.getPlayer();
        String message = event.getMessage();
        message = message.replace(config.getString("appearance.staffchat-symbol"), "");
        String mcFormat = config.getString("appearance.mc-format");
        mcFormat = mcFormat.replace("{player}", player.getUsername());
        mcFormat = mcFormat.replace("{message}", message);
        mcFormat = mcFormat.replace("{prefix}", LuckPermsUtil.getPrefix(player));
        String mcToDiscord = config.getString("appearance.mc-to-discord");
        mcToDiscord = mcToDiscord.replace("{player}", player.getUsername());
        mcToDiscord = mcToDiscord.replace("{message}", message);
        TextChannel staffChat = StaffChat.getInstance().getJda().getTextChannelById(config.getString("discord.staff-channel"));
        String finalMcFormat = mcFormat;
        if(ToggleStaffChat.toggleStaffList.contains(player.getUniqueId()))
        {
            sendStaffMessage(event, player, mcToDiscord, staffChat, finalMcFormat);
        } else if(event.getMessage().startsWith(config.getString("appearance.staffchat-symbol")))
        {
            sendStaffMessage(event, player, mcToDiscord, staffChat, finalMcFormat);
        }
    }

    private void sendStaffMessage(PlayerChatEvent event, Player player, String mcToDiscord, TextChannel staffChat, String finalMcFormat) {
        if(player.hasPermission(Permissions.STAFFCHAT_USE))
        {
            event.setResult(PlayerChatEvent.ChatResult.message(""));
            staffChat.sendMessageFormat(mcToDiscord).queue();
            StaffChat.getInstance().getServer().getAllPlayers().forEach(allPlayers ->
            {
                if(allPlayers.hasPermission(Permissions.STAFFCHAT_SEE))
                {
                    if(!(mutedStaffList.contains(allPlayers.getUniqueId()))) {
                        allPlayers.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(finalMcFormat));
                    }
                }
            });
        }
    }

    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(!event.getMessage().getAuthor().isBot()) {
            String discordToMc = config.getString("appearance.discord-to-mc");
            discordToMc = discordToMc.replace("{player}", event.getMember().getEffectiveName());
            discordToMc = discordToMc.replace("{message}", event.getMessage().getContentDisplay());
            String finalDiscordToMc = discordToMc;
            StaffChat.getInstance().getServer().getAllPlayers().forEach(player ->
            {
                if (player.hasPermission(Permissions.STAFFCHAT_SEE))
                {
                    player.sendMessage(MiniMessage.get().deserialize(finalDiscordToMc));
                }
            });
        }
    }
}
