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
public class CapabilityPassword {
    public static class Storage implements Capability.IStorage<IPassword> {
        @Override
        public NBTBase writeNBT(Capability<IPassword> capability, IPassword instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("pwd", instance.getPassword());
            nbt.setBoolean("first", instance.isFirst());
            nbt.setInteger("gameType", instance.getGameType());
            return nbt;
        }

        @Override
        public void readNBT(Capability<IPassword> capability, IPassword instance, EnumFacing side, NBTBase nbt) {
            instance.setPassword(((NBTTagCompound) nbt).getString("pwd"));
            instance.setFirst(((NBTTagCompound) nbt).getBoolean("first"));
            instance.setGameType(((NBTTagCompound) nbt).getInteger("gameType"));
        }
    }

    public static class Implementation implements IPassword {
        String pwd = "";
        boolean first = true;
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
        public boolean isFirst() {
            return first;
        }

        @Override
        public void setFirst(boolean first) {
            this.first = first;
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

    public static class PlayerProvider implements ICapabilitySerializable<NBTTagCompound> {
        private IPassword password = new Implementation();
        private Capability.IStorage<IPassword> storage = CapabilityLoader.CAPABILITY_PASSWORD.getStorage();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityLoader.CAPABILITY_PASSWORD;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityLoader.CAPABILITY_PASSWORD) {
                return (T) password;
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) storage.writeNBT(CapabilityLoader.CAPABILITY_PASSWORD, password, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            storage.readNBT(CapabilityLoader.CAPABILITY_PASSWORD, password, null, nbt);
        }
    }
}
