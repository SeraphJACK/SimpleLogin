package top.seraphjack.simplelogin.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.SERVER)
public class CapabilitySLEntry {
    public static class Storage implements Capability.IStorage<ISLEntry> {
        @Override
        public NBTBase writeNBT(Capability<ISLEntry> capability, ISLEntry instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("pwd", instance.getPassword());
            nbt.setInteger("gameType", instance.getGameType());
            return nbt;
        }

        @Override
        public void readNBT(Capability<ISLEntry> capability, ISLEntry instance, EnumFacing side, NBTBase nbt) {
            instance.setPassword(((NBTTagCompound) nbt).getString("pwd"));
            instance.setGameType(((NBTTagCompound) nbt).getInteger("gameType"));
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

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final ISLEntry password = new Implementation();
        private final Capability.IStorage<ISLEntry> storage = CapabilityLoader.CAPABILITY_SL_ENTRY.getStorage();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityLoader.CAPABILITY_SL_ENTRY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityLoader.CAPABILITY_SL_ENTRY) {
                return (T) password;
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) storage.writeNBT(CapabilityLoader.CAPABILITY_SL_ENTRY, password, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            storage.readNBT(CapabilityLoader.CAPABILITY_SL_ENTRY, password, null, nbt);
        }
    }
}
