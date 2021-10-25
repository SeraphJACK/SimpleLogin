package top.seraphjack.simplelogin.server.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import top.seraphjack.simplelogin.server.storage.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.DEDICATED_SERVER)
public class CapabilityLastPos {
    public static final Position defaultPosition = new Position(0, 255, 0);

    public static class Provider implements ICapabilitySerializable<Tag> {
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
        public Tag serializeNBT() {
            return lastPos.getLastPos().toNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            this.lastPos.setLastPos(Position.fromNBT((CompoundTag) nbt));
        }
    }

    public static class Implementation implements ILastPos {
        Position lastPos = defaultPosition;

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
