package top.seraphjack.simplelogin.server.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import top.seraphjack.simplelogin.SLConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySLEntry {
    public static class Storage implements Capability.IStorage<ISLEntry> {
        @Override
        public INBT writeNBT(Capability<ISLEntry> capability, ISLEntry instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("pwd", instance.getPassword());
            nbt.putInt("gameType", instance.getGameType());
            return nbt;
        }

        @Override
        public void readNBT(Capability<ISLEntry> capability, ISLEntry instance, Direction side, INBT nbt) {
            instance.setPassword(((CompoundNBT) nbt).getString("pwd"));
            instance.setGameType(((CompoundNBT) nbt).getInt("gameType"));
        }
    }

    public static class Implementation implements ISLEntry {
        String pwd = "";
        int gameType = SLConfig.server.defaultGameType;

        @Override
        public String getPassword() {
            return pwd;
        }

        @Override
        public void setPassword(String password) {
            this.pwd = password;
        }

        @Override
        public void setGameType(int gameType) {
            this.gameType = gameType;
        }

        @Override
        public int getGameType() {
            return this.gameType;
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT> {
        private ISLEntry password = new Implementation();
        private Capability.IStorage<ISLEntry> storage = CapabilityLoader.CAPABILITY_SL_ENTRY.getStorage();

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return (LazyOptional<T>) LazyOptional.of(() -> password);
        }

        @Override
        public CompoundNBT serializeNBT() {
            return (CompoundNBT) storage.writeNBT(CapabilityLoader.CAPABILITY_SL_ENTRY, password, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            storage.readNBT(CapabilityLoader.CAPABILITY_SL_ENTRY, password, null, nbt);
        }
    }
}
