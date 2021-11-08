package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class Timeout implements HandlerPlugin {
    private ScheduledExecutorService executor;
    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    @Override
    public void enable(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void preLogin(ServerPlayer player, Login login) {
        ScheduledFuture<?> future = executor.schedule(() -> {
            player.connection.disconnect(new TextComponent("Login timeout"));
        }, SLConfig.SERVER.secs.get(), TimeUnit.SECONDS);

        Optional.ofNullable(futures.put(login.name, future))
                .ifPresent(f -> f.cancel(true));
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        Optional.ofNullable(futures.remove(login.name)).ifPresent(f -> f.cancel(true));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        Optional.ofNullable(futures.remove(player.getGameProfile().getName().toLowerCase()))
                .ifPresent(f -> f.cancel(true));
    }

    @Override
    public void disable() {
        futures.values().forEach(f -> f.cancel(true));
    }
}
