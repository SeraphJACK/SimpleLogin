package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
import top.seraphjack.simplelogin.server.storage.SLStorage;

public final class RestrictGameType implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayer player, Login login) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(1, () -> {
            player.setGameMode(GameType.SPECTATOR);
        }));
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(1, () -> {
            player.setGameMode(SLStorage.instance().storageProvider.gameType(player.getGameProfile().getName()));
        }));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        player.setGameMode(GameType.SPECTATOR);
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
