package top.seraphjack.simplelogin.server.handler.plugins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.Login;
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
                    // FIXME this is somehow broken and I don't really know why
                    .orElseThrow(RuntimeException::new).getLastPos();

            if (lastPos.equals(defaultPosition)) {
                player.teleport(player.getServerWorld(), login.posX, login.posY, login.posZ, login.yaw, login.pitch);
            } else {
                player.teleport(player.getServerWorld(), lastPos.getX(), lastPos.getY(), lastPos.getZ(), login.yaw, login.pitch);
            }
        });
    }

    @Override
    public void preLogout(ServerPlayerEntity player) {
        try {
            player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS)
                    .orElseThrow(RuntimeException::new)
                    .setLastPos(new Position(player.getPosX(), player.getPosY(), player.getPosZ()));
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
