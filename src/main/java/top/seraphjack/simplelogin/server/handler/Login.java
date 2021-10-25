package top.seraphjack.simplelogin.server.handler;

import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public final class Login {
    public final String name;
    public final long time;
    public final double posX, posY, posZ;
    public final float yaw, pitch;

    Login(ServerPlayer player) {
        this.name = player.getGameProfile().getName().toLowerCase();
        this.time = System.currentTimeMillis();
        this.posX = player.getX();
        this.posY = player.getY();
        this.posZ = player.getZ();
        this.yaw = player.getYRot();
        this.pitch = player.getXRot();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Login login = (Login) o;
        return time == login.time && Double.compare(login.posX, posX) == 0 && Double.compare(login.posY, posY) == 0 && Double.compare(login.posZ, posZ) == 0 && Float.compare(login.yaw, yaw) == 0 && Float.compare(login.pitch, pitch) == 0 && name.equals(login.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, posX, posY, posZ, yaw, pitch);
    }
}
