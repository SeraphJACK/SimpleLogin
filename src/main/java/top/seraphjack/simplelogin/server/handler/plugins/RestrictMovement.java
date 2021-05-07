package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class RestrictMovement implements HandlerPlugin {
    private ScheduledExecutorService executor;
    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    @Override
    public void enable(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void preLogin(ServerPlayerEntity player, Login login) {
        ScheduledFuture<?> future = executor.scheduleWithFixedDelay(() -> {
            ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
                player.setPosition(login.posX, login.posY, login.posZ);
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
        Optional.ofNullable(futures.put(login.name, future)).ifPresent(f -> f.cancel(true));
    }

    @Override
    public void postLogin(ServerPlayerEntity player, Login login) {
        Optional.ofNullable(futures.remove(login.name)).ifPresent(f -> f.cancel(true));
    }

    @Override
    public void preLogout(ServerPlayerEntity player) {
        Optional.ofNullable(futures.remove(player.getGameProfile().getName()))
                .ifPresent(f -> f.cancel(true));
    }
}
