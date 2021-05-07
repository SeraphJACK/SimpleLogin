package top.seraphjack.simplelogin.server.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.SLRegistries;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

@OnlyIn(Dist.DEDICATED_SERVER)
public final class PlayerLoginHandler {
    private static PlayerLoginHandler INSTANCE;

    private final Set<Login> loginList = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder()
            .setNameFormat("SimpleLogin-Worker-%d")
            .build());
    private final List<Pair<ResourceLocation, HandlerPlugin>> plugins = new LinkedList<>();

    private PlayerLoginHandler(Collection<ResourceLocation> plugins) {
        // Load plugins
        for (ResourceLocation aRl : plugins) {
            loadPlugin(aRl);
        }
    }

    public void loadPlugin(ResourceLocation rl) {
        if (this.plugins.stream().anyMatch(p -> p.getFirst().equals(rl))) return;
        SimpleLogin.logger.info("Loading plugin {}", rl.toString());
        HandlerPlugin plugin = SLRegistries.PLUGINS.get(rl).orElseThrow(() -> {
            return new IllegalArgumentException("No such plugin found: " + rl);
        }).get();

        this.plugins.add(Pair.of(rl, plugin));
        plugin.enable(executor);
    }

    public void unloadPlugin(ResourceLocation rl) {
        Optional<HandlerPlugin> plugin = this.plugins.stream().filter(p -> p.getFirst().equals(rl)).map(Pair::getSecond).findAny();
        if (this.plugins.removeIf(p -> p.getFirst().equals(rl))) {
            plugin.ifPresent(HandlerPlugin::disable);
            SimpleLogin.logger.info("Unloaded plugin {}", rl.toString());
        }
    }

    public Collection<ResourceLocation> listPlugins() {
        return this.plugins.stream().map(Pair::getFirst).collect(Collectors.toSet());
    }

    public static void initLoginHandler(Collection<ResourceLocation> pluginList) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new PlayerLoginHandler(pluginList);
    }

    // Singleton
    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public void login(String id, String pwd) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Login login = getLoginByName(id);
        ServerPlayerEntity player = server.getPlayerList().getPlayerByUsername(id);

        // Though player shouldn't be null if login is not null
        if (login == null || player == null) {
            return;
        }

        if (pwd.length() >= 100) {
            player.connection.disconnect(new StringTextComponent("Password too long."));
            SimpleLogin.logger.warn("Player " + id + " tried to login with a invalid password(too long).");
        } else if (!SLStorage.instance().storageProvider.registered(id)) {
            SLStorage.instance().storageProvider.register(id, pwd);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
            postLogin(player, login);
        } else if (SLStorage.instance().storageProvider.checkPassword(id, pwd)) {
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
            postLogin(player, login);
        } else {
            SimpleLogin.logger.warn("Player " + id + " tried to login with a wrong password.");
            player.connection.disconnect(new StringTextComponent("Wrong Password."));
        }

        loginList.remove(login);
    }

    public void playerJoin(final ServerPlayerEntity player) {
        Login login = new Login(player);
        loginList.add(login);
        plugins.forEach(p -> p.getSecond().preLogin(player, login));
    }

    public void playerLeave(final ServerPlayerEntity player) {
        loginList.removeIf(l -> l.name.equals(player.getGameProfile().getName()));
        plugins.forEach(p -> p.getSecond().preLogout(player));
    }

    public void postLogin(final ServerPlayerEntity player, final Login login) {
        plugins.forEach(p -> p.getSecond().postLogin(player, login));
    }

    public boolean hasPlayerLoggedIn(String id) {
        return loginList.stream().noneMatch(e -> e.name.equals(id));
    }

    public void stop() {
        SimpleLogin.logger.info("Shutting down player login handler");
        SimpleLogin.logger.info("Disabling all plugins...");
        this.plugins.stream().map(Pair::getSecond).forEach(HandlerPlugin::disable);
        this.plugins.clear();
        executor.shutdown();
    }

    @Nullable
    private Login getLoginByName(String name) {
        return loginList.stream().filter(l -> l.name.equals(name)).findAny().orElse(null);
    }
}
