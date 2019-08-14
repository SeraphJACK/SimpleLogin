package top.seraphjack.simplelogin.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.storage.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.SERVER)
public class CapabilityPassword {
    public static class Storage implements Capability.IStorage<ISLEntry> {
        @Override
        public NBTBase writeNBT(Capability<ISLEntry> capability, ISLEntry instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("pwd", instance.getPassword());
            nbt.setBoolean("first", instance.isFirst());
            nbt.setInteger("gameType", instance.getGameType());
            nbt.setDouble("posX", instance.getLastPosition().getX());
            nbt.setDouble("posY", instance.getLastPosition().getY());
            nbt.setDouble("posZ", instance.getLastPosition().getZ());
            return nbt;
        }

        @Override
        public void readNBT(Capability<ISLEntry> capability, ISLEntry instance, EnumFacing side, NBTBase nbt) {
            instance.setPassword(((NBTTagCompound) nbt).getString("pwd"));
            instance.setFirst(((NBTTagCompound) nbt).getBoolean("first"));
            instance.setGameType(((NBTTagCompound) nbt).getInteger("gameType"));
            instance.setLastPosition(new Position(
                    ((NBTTagCompound) nbt).getDouble("posX"),
                    ((NBTTagCompound) nbt).getDouble("posY"),
                    ((NBTTagCompound) nbt).getDouble("posZ")
            ));
        }
    }

    public static class Implementation implements ISLEntry {
        String pwd = "";
        boolean first = true;
        int gameType = SLConfig.server.defaultGameType;
        Position pos;

        public Implementation() {
            setLastPosition(SLConstants.defaultPosition);
        }

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

        @Override
        public Position getLastPosition() {
            return null;
        }

        @Override
        public void setLastPosition(Position pos) {
            this.pos = pos;
        }
    }

    public static class PlayerProvider implements ICapabilitySerializable<NBTTagCompound> {
        private ISLEntry password = new Implementation();
        private Capability.IStorage<ISLEntry> storage = CapabilityLoader.CAPABILITY_PASSWORD.getStorage();

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
