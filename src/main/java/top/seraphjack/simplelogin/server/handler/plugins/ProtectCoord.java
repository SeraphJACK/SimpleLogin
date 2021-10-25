package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.server.storage.Position;

import static top.seraphjack.simplelogin.server.capability.CapabilityLastPos.defaultPosition;

public final class ProtectCoord implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayer player, Login login) {
        // NO-OP
    }

    @Override
    public void postLogin(ServerPlayer player, Login login) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(1, () -> {
            Position lastPos = player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS)
                    .orElseThrow(RuntimeException::new).getLastPos();

            if (lastPos.equals(defaultPosition)) {
                player.setPos(login.posX, login.posY, login.posZ);
            } else {
                player.setPos(lastPos.getX(), lastPos.getY(), lastPos.getZ());
            }
        }));
    }

    @Override
    public void preLogout(ServerPlayer player) {
        try {
            if (PlayerLoginHandler.instance().hasPlayerLoggedIn(player.getGameProfile().getName())) {
                final Position pos = new Position(player.getX(), player.getY(), player.getZ());
                player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS).ifPresent(c -> c.setLastPos(pos));
            }
            BlockPos spawnPoint = player.getLevel().getSharedSpawnPos();
            player.setPos(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
        } catch (Exception ex) {
            SimpleLogin.logger.error("Fail to set player position to spawn point when logging out.", ex);
        }
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
