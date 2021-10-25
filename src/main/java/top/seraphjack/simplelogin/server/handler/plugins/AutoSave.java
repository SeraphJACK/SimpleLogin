package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.server.level.ServerPlayer;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class AutoSave implements HandlerPlugin {
    private ScheduledFuture<?> future;

    @Override
    public void enable(ScheduledExecutorService executor) {
        this.future = executor.scheduleAtFixedRate(() -> {
            if (SLStorage.instance().storageProvider.dirty()) {
                SimpleLogin.logger.info("Auto saving entries");
                long start = System.currentTimeMillis();
                try {
                    SLStorage.instance().storageProvider.save();
                } catch (IOException e) {
                    SimpleLogin.logger.error("Failed saving simple login entries", e);
                }
                SimpleLogin.logger.info("Done! Took " + (System.currentTimeMillis() - start) + "ms.");
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void preLogin(ServerPlayer player, Login login) {
        // NO-OP
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        // NO-OP
    }

    @Override
    public void preLogout(ServerPlayer player) {
        // NO-OP
    }

    @Override
    public void disable() {
        future.cancel(true);
    }
}
