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

package net.ryzech.staffchat;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.ryzech.staffchat.commands.MuteStaffChat;
import net.ryzech.staffchat.commands.ToggleStaffChat;
import net.ryzech.staffchat.listeners.ChatListener;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Plugin(
        id = "staffchat",
        name = "StaffChat",
        version = BuildConstants.VERSION,
        description = "StaffChat plugin for Velocity!",
        url = "https://ryzech.net",
        authors = {"RyzechDev"}
)
public class StaffChat {

    private static StaffChat instance;

    private final ProxyServer server;
    private final Path dataDirectory;
    private Logger logger;
    private Toml config;
    private final Metrics.Factory metricsFactory;
    private JDA jda;
    private Instant startTime;

    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        return new Toml().read(file);
    }

    @Inject
    public StaffChat(CommandManager commandManager, ProxyServer server, @DataDirectory Path dataDirectory, Logger logger, Metrics.Factory metricsFactory) {
        setInstance(this);

        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.metricsFactory = metricsFactory;

        try {
            Class.forName("com.velocitypowered.proxy.connection.client.LoginInboundConnection");
        } catch (ClassNotFoundException e) {
            this.getLogger().error("Please update your Velocity binary to 3.1.x", e);
            this.server.shutdown();
        }
        config = loadConfig(dataDirectory);
        commandManager.register("sctoggle", new ToggleStaffChat());
        commandManager.register("scmute", new MuteStaffChat());
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws LoginException {
        this.startTime = Instant.now();
        getLogger().info("Plugin enabled (took " + startTime + ")");
        Metrics metrics = metricsFactory.make(this, 13997);
        metrics.addCustomChart(new SimplePie("playerAmount", () -> String.valueOf(server.getPlayerCount())));
        metrics.addCustomChart(new SimplePie("velocityVersion", () -> server.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("javaVersion", () -> System.getProperty("java.version")));
        metrics.addCustomChart(new SimplePie("osName", () -> System.getProperty("os.name")));
        metrics.addCustomChart(new SimplePie("osArch", () -> System.getProperty("os.arch")));
        metrics.addCustomChart(new SimplePie("osVersion", () -> System.getProperty("os.version")));
        metrics.addCustomChart(new SimplePie("coreCount", () -> String.valueOf(Runtime.getRuntime().availableProcessors())));
        server.getEventManager().register(this, new ChatListener());
        jda = JDABuilder.createDefault(config.getString("discord.token")).build();
        jda.getPresence().setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("discord.activity-type").toUpperCase()), config.getString("discord.activity")));
        jda.addEventListener(new ChatListener());
    }

    private static void setInstance(StaffChat instance) {
        StaffChat.instance = instance;
    }

    public static StaffChat getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Toml getConfig() {
        return config;
    }

    public JDA getJda() { return jda; }

    public ProxyServer getServer() {
        return server;
    }

    public @NonNull LuckPerms getLuckPermsProvider() {
        return LuckPermsProvider.get();
    }

}
