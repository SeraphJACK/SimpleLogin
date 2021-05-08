package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
import top.seraphjack.simplelogin.server.storage.SLStorage;

public final class RestrictGameType implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayerEntity player, Login login) {
        ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
            player.setGameType(GameType.SPECTATOR);
        });
    }

    @Override
    public void postLogin(ServerPlayerEntity player, Login login) {
        ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
            player.setGameType(SLStorage.instance().storageProvider.gameType(player.getGameProfile().getName()));
        });
    }

    @Override
    public void preLogout(ServerPlayerEntity player) {
        player.setGameType(GameType.SPECTATOR);
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
