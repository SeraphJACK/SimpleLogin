package top.seraphjack.simplelogin.server.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class Position {
    private final double x, y, z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position cast = (Position) o;
            return x == cast.getX() && y == cast.getY() && z == cast.getZ();
        }
        return false;
    }

    public INBT toNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        return tag;
    }

    public static Position fromNBT(CompoundNBT nbt) {
        return new Position(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
