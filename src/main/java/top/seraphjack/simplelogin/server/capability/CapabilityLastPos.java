package top.seraphjack.simplelogin.server.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.storage.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.DEDICATED_SERVER)
public class CapabilityLastPos {
    public static class Storage implements Capability.IStorage<ILastPos> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<ILastPos> capability, ILastPos instance, Direction side) {
            return instance.getLastPos().toNBT();
        }

        @Override
        public void readNBT(Capability<ILastPos> capability, ILastPos instance, Direction side, INBT nbt) {
            instance.setLastPos(Position.fromNBT((CompoundNBT) nbt));
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT> {
        private final ILastPos lastPos = new Implementation();

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            if (capability == CapabilityLoader.CAPABILITY_LAST_POS)
                return (LazyOptional<T>) LazyOptional.of(() -> lastPos);
            return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return CapabilityLoader.CAPABILITY_LAST_POS.getStorage().writeNBT(CapabilityLoader.CAPABILITY_LAST_POS, lastPos, null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            CapabilityLoader.CAPABILITY_LAST_POS.getStorage().readNBT(CapabilityLoader.CAPABILITY_LAST_POS, lastPos, null, nbt);
        }
    }

    public static class Implementation implements ILastPos {
        Position lastPos = SLConstants.defaultPosition;

        @Override
        public Position getLastPos() {
            return lastPos;
        }

        @Override
        public void setLastPos(Position pos) {
            lastPos = pos;
        }
    }
}
