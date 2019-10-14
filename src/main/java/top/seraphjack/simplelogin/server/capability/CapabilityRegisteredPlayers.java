package top.seraphjack.simplelogin.server.capability;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public class CapabilityRegisteredPlayers {
    public static class Storage implements Capability.IStorage<IRegisteredPlayers> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IRegisteredPlayers> capability, IRegisteredPlayers instance, EnumFacing side) {
            NBTTagList tag = new NBTTagList();
            for (String id : instance.getRegisteredPlayers()) {
                tag.appendTag(new NBTTagString(id));
            }
            return tag;
        }

        @Override
        public void readNBT(Capability<IRegisteredPlayers> capability, IRegisteredPlayers instance, EnumFacing side, NBTBase nbt) {
            for (NBTBase tag : (NBTTagList) nbt) {
                instance.add(((NBTTagString) tag).getString());
            }
        }
    }

    public static class Implementation implements IRegisteredPlayers {
        private Collection<String> players = new HashSet<>();

        @Override
        public Collection<String> getRegisteredPlayers() {
            return new ImmutableSet.Builder<String>().addAll(players).build();
        }

        @Override
        public void add(String name) {
            players.add(name);
        }

        @Override
        public void remove(String name) {
            players.remove(name);
        }

        @Override
        public boolean contains(String name) {
            return players.contains(name);
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagList> {
        private IRegisteredPlayers registeredPlayers = new Implementation();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (hasCapability(capability,facing)) {
                return (T) registeredPlayers;
            }
            return null;
        }

        @Override
        public NBTTagList serializeNBT() {
            return (NBTTagList) CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS.getStorage().writeNBT(CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS,registeredPlayers,null);
        }

        @Override
        public void deserializeNBT(NBTTagList nbt) {
            CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS.getStorage().readNBT(CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS,registeredPlayers,null,nbt);
        }
    }
}
