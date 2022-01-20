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

public class ChatListener extends ListenerAdapter {
    public Toml config = StaffChat.getInstance().getConfig();

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event)
    {
        Player player = (Player) event.getPlayer();
        String message = event.getMessage();
        message = message.replace(config.getString("appearance.staffchat-symbol"), "");
        String mcFormat = config.getString("appearance.mc-format");
        mcFormat = mcFormat.replace("{player}", player.getUsername());
        mcFormat = mcFormat.replace("{message}", message);
        mcFormat = mcFormat.replace("{prefix}", StaffChat.getInstance().getPrefix(player));
        String mcToDiscord = config.getString("appearance.mc-to-discord");
        mcToDiscord = mcToDiscord.replace("{player}", player.getUsername());
        mcToDiscord = mcToDiscord.replace("{message}", message);
        TextChannel staffChat = StaffChat.getInstance().getJda().getTextChannelById(config.getString("discord.staff-channel"));
        if(!(player instanceof Player))
        {
            return;
        } else
        {
            if(ToggleStaffChat.toggleStaffList.contains(player.getUniqueId()))
            {
                event.setResult(PlayerChatEvent.ChatResult.message(""));
                player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mcFormat));
                staffChat.sendMessageFormat(mcToDiscord).queue();
            } else if(event.getMessage().startsWith(config.getString("appearance.staffchat-symbol")))
            {
                if(player.hasPermission("staffchat.admin"))
                {
                    event.setResult(PlayerChatEvent.ChatResult.message(""));
                    player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mcFormat));
                    staffChat.sendMessageFormat(mcToDiscord).queue();
                }
            }
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
                if (player.hasPermission("staffchat.admin"))
                {
                    player.sendMessage(MiniMessage.get().deserialize(finalDiscordToMc));
                }
            });
        }
    }
}
