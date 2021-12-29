package top.seraphjack.simplelogin.server.handler;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.SLRegistries;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@OnlyIn(Dist.DEDICATED_SERVER)
public final class PlayerLoginHandler {
    private static PlayerLoginHandler INSTANCE;

    private final Set<Login> loginList = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder()
            .setNameFormat("SimpleLogin-Worker-%d")
            .build());
    private final Map<ResourceLocation, HandlerPlugin> plugins = new ConcurrentHashMap<>();

    private PlayerLoginHandler(Stream<ResourceLocation> plugins) {
        // Load plugins
        plugins.forEach(this::loadPlugin);
    }

    public void loadPlugin(ResourceLocation rl) {
        if (this.plugins.containsKey(rl)) return;
        SimpleLogin.logger.info("Loading plugin {}", rl.toString());
        HandlerPlugin plugin = SLRegistries.PLUGINS.get(rl).orElseThrow(() -> {
            return new IllegalArgumentException("No such plugin found: " + rl);
        }).get();

        // Should not be possible though
        Optional.ofNullable(this.plugins.put(rl, plugin)).ifPresent(HandlerPlugin::disable);
        plugin.enable(executor);
    }

    public void unloadPlugin(ResourceLocation rl) {
        Optional.ofNullable(plugins.remove(rl)).ifPresent(p -> {
            p.disable();
            SimpleLogin.logger.info("Unloaded plugin {}", rl.toString());
        });
    }

    public Collection<ResourceLocation> listPlugins() {
        return new ImmutableSet.Builder<ResourceLocation>().addAll(this.plugins.keySet()).build();
    }

    public static void initLoginHandler(Stream<ResourceLocation> pluginList) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new PlayerLoginHandler(pluginList);
    }

    // Singleton
    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public void login(String id, String pwd) {
        id = id.toLowerCase();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Login login = getLoginByName(id);
        ServerPlayer player = server.getPlayerList().getPlayerByName(id);

        // Though player shouldn't be null if login is not null
        if (login == null || player == null) {
            return;
        }

        loginList.remove(login);

        if (!SLStorage.instance().storageProvider.registered(id)) {
            SLStorage.instance().storageProvider.register(id, pwd);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
            postLogin(player, login);
        } else if (SLStorage.instance().storageProvider.checkPassword(id, pwd)) {
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
            postLogin(player, login);
        } else {
            SimpleLogin.logger.warn("Player " + id + " tried to login with a wrong password.");
            player.connection.disconnect(new TextComponent("Wrong Password."));
        }
    }

    public void playerJoin(final ServerPlayer player) {
        Login login = new Login(player);
        loginList.add(login);
        plugins.values().forEach(p -> p.preLogin(player, login));
    }

    public void playerLeave(final ServerPlayer player) {
        loginList.removeIf(l -> l.name.equals(player.getGameProfile().getName()));
        plugins.values().forEach(p -> p.preLogout(player));
    }

    public void postLogin(final ServerPlayer player, final Login login) {
        plugins.values().forEach(p -> p.postLogin(player, login));
    }

    public boolean hasPlayerLoggedIn(String id) {
        return loginList.stream().noneMatch(e -> e.name.equals(id.toLowerCase()));
    }

    public void stop() {
        SimpleLogin.logger.info("Shutting down player login handler");
        SimpleLogin.logger.info("Disabling all plugins");
        this.plugins.values().forEach(HandlerPlugin::disable);
        this.plugins.clear();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                SimpleLogin.logger.error("Timed out waiting player login handler to terminate.");
            }
        } catch (InterruptedException ignore) {
            SimpleLogin.logger.error("Interrupted when waiting player login handler to terminate.");
        }
    }

    @Nullable
    private Login getLoginByName(String name) {
        return loginList.stream().filter(l -> l.name.equals(name)).findAny().orElse(null);
    }
}
