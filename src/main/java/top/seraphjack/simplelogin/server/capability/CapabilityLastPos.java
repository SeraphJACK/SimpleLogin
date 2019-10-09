package top.seraphjack.simplelogin.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.storage.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityLastPos {
    public static class Storage implements Capability.IStorage<ILastPos> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<ILastPos> capability, ILastPos instance, EnumFacing side) {
            return instance.getLastPos().toNBT();
        }

        @Override
        public void readNBT(Capability<ILastPos> capability, ILastPos instance, EnumFacing side, NBTBase nbt) {
            instance.setLastPos(Position.fromNBT((NBTTagCompound) nbt));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private ILastPos lastPos = new Implementation();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityLoader.CAPABILITY_LAST_POS;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (hasCapability(capability, facing)) {
                return (T) lastPos;
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) CapabilityLoader.CAPABILITY_LAST_POS.getStorage().writeNBT(CapabilityLoader.CAPABILITY_LAST_POS, lastPos, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
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
