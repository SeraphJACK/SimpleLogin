package top.seraphjack.simplelogin.server.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class Login {
    public final String name;
    public final long time;
    public final double posX, posY, posZ;
    public final float yaw, pitch;

    Login(ServerPlayerEntity player) {
        this.name = player.getGameProfile().getName();
        this.time = System.currentTimeMillis();
        this.posX = player.getPosX();
        this.posY = player.getPosY();
        this.posZ = player.getPosZ();
        this.yaw = player.rotationYaw;
        this.pitch = player.rotationPitch;
    }
}
