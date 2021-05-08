package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.server.storage.Position;

import static top.seraphjack.simplelogin.server.capability.CapabilityLastPos.defaultPosition;

public final class ProtectCoord implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayerEntity player, Login login) {
        // NO-OP
    }

    @Override
    public void postLogin(ServerPlayerEntity player, Login login) {
        ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
            Position lastPos = player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS)
                    .orElseThrow(RuntimeException::new).getLastPos();

            if (lastPos.equals(defaultPosition)) {
                player.setPositionAndUpdate(login.posX, login.posY, login.posZ);
            } else {
                player.setPositionAndUpdate(lastPos.getX(), lastPos.getY(), lastPos.getZ());
            }
        });
    }

    @Override
    public void preLogout(ServerPlayerEntity player) {
        try {
            if (PlayerLoginHandler.instance().hasPlayerLoggedIn(player.getGameProfile().getName())) {
                final Position pos = new Position(player.getPosX(), player.getPosY(), player.getPosZ());
                player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS).ifPresent(c -> c.setLastPos(pos));
            }
            IWorldInfo info = player.getServerWorld().getWorldInfo();
            player.setPosition(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
        } catch (Exception ex) {
            SimpleLogin.logger.error("Fail to set player position to spawn point when logging out.", ex);
        }
    }

    @Override
    public void disable() {
        // NO-OP
    }
}
