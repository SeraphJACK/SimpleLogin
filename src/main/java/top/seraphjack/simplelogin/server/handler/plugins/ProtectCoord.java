package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.storage.IWorldInfo;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
import top.seraphjack.simplelogin.server.storage.Position;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import static top.seraphjack.simplelogin.server.capability.CapabilityLastPos.defaultPosition;

public final class ProtectCoord implements HandlerPlugin {
    @Override
    public void preLogin(ServerPlayerEntity player, Login login) {
        // NO-OP
    }

    @Override
    public void postLogin(ServerPlayerEntity player, Login login) {
        player.setGameType(SLStorage.instance().storageProvider.gameType(login.name));
        if (SLConfig.SERVER.protectPlayerCoord.get()) {
            Position lastPos = player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS)
                    .orElseThrow(RuntimeException::new).getLastPos();

            if (lastPos.equals(defaultPosition)) {
                player.setPosition(login.posX, login.posY, login.posZ);
            } else {
                player.setPositionAndUpdate(lastPos.getX(), lastPos.getY(), lastPos.getZ());
            }
        }
    }

    @Override
    public void preLogout(ServerPlayerEntity player) {
        try {
            IWorldInfo info = player.getServerWorld().getWorldInfo();
            player.setPosition(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
        } catch (Exception ex) {
            SimpleLogin.logger.error("Fail to set player position to spawn point when logging out.", ex);
        }
    }
}
