package net.ryzech.staffchat;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.ryzech.staffchat.commands.ToggleStaffChat;
import net.ryzech.staffchat.listeners.ChatListener;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
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

    public String getPrefix(Player player) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user.getCachedData().getMetaData().getPrefix() == null)
            return "";
        return user.getCachedData().getMetaData().getPrefix();
    }

}
