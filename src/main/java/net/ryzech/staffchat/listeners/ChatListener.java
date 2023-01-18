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

package net.ryzech.staffchat.listeners;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
        String message = event.getResult().getMessage().toString();
        message = message.replace(config.getString("appearance.staffchat-symbol"), "");
        String mcFormat = config.getString("appearance.mc-format");
        mcFormat = mcFormat.replace("{player}", player.getUsername());
        mcFormat = mcFormat.replace("{message}", message);
        mcFormat = mcFormat.replace("{prefix}", LuckPermsUtil.getPrefix(player));
        mcFormat = mcFormat.replace("{suffix}", LuckPermsUtil.getSuffix(player));
        String mcToDiscord = config.getString("appearance.mc-to-discord");
        mcToDiscord = mcToDiscord.replace("{player}", player.getUsername());
        mcToDiscord = mcToDiscord.replace("{message}", message);
        TextChannel staffChat = StaffChat.getInstance().getJda().getTextChannelById(config.getString("discord.staff-channel"));
        String finalMcFormat = mcFormat;
        if(ToggleStaffChat.toggleStaffList.contains(player.getUniqueId()))
            sendStaffMessage(event, player, mcToDiscord, staffChat, finalMcFormat);
        else if(event.getMessage().startsWith(config.getString("appearance.staffchat-symbol")))
            sendStaffMessage(event, player, mcToDiscord, staffChat, finalMcFormat);
    }

    private void sendStaffMessage(PlayerChatEvent event, Player player, String mcToDiscord, TextChannel staffChat, String finalMcFormat) {
        if(player.hasPermission(Permissions.STAFFCHAT_USE))
        {
            event.setResult(PlayerChatEvent.ChatResult.message(""));
            staffChat.sendMessageFormat(mcToDiscord).queue();
            StaffChat.getInstance().getServer().getAllPlayers().forEach(allPlayers ->
            {
                if(allPlayers.hasPermission(Permissions.STAFFCHAT_SEE))
                    allPlayers.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(finalMcFormat));
            });
        }
    }

    //TODO: test & figure out if message content will be sent through to minecraft with these small changes.
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(!event.getMessage().getAuthor().isBot()) {
            String discordToMc = config.getString("appearance.discord-to-mc");
            discordToMc = discordToMc.replace("{player}", event.getMember().getNickname());
            discordToMc = discordToMc.replace("{message}", event.getMessage().getContentStripped());
            String finalDiscordToMc = discordToMc;
            if(event.getChannel().getId().equalsIgnoreCase(config.getString("discord.staff-channel")))
                StaffChat.getInstance().getServer().getAllPlayers().forEach(player ->
                {
                    if (player.hasPermission(Permissions.STAFFCHAT_SEE))
                        player.sendMessage(MiniMessage.miniMessage().deserialize(finalDiscordToMc));
                });
        }
    }
}
