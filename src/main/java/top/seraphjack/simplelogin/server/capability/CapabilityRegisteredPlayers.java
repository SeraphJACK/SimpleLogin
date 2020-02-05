package top.seraphjack.simplelogin.server.capability;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public class CapabilityRegisteredPlayers {
    public static class Storage implements Capability.IStorage<IRegisteredPlayers> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IRegisteredPlayers> capability, IRegisteredPlayers instance, Direction side) {
            ListNBT tag = new ListNBT();
            for (String id : instance.getRegisteredPlayers()) {
                tag.add(new StringNBT(id));
            }
            return tag;
        }

        @Override
        public void readNBT(Capability<IRegisteredPlayers> capability, IRegisteredPlayers instance, Direction side, INBT nbt) {
            for (INBT tag : (ListNBT) nbt) {
                instance.add(((StringNBT) tag).getString());
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

    public static class Provider implements ICapabilitySerializable<ListNBT> {
        private IRegisteredPlayers registeredPlayers = new Implementation();

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return (LazyOptional<T>) LazyOptional.of(() -> registeredPlayers);
        }

        @Override
        public ListNBT serializeNBT() {
            return (ListNBT) CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS.getStorage().writeNBT(CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS,registeredPlayers,null);
        }

        @Override
        public void deserializeNBT(ListNBT nbt) {
            CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS.getStorage().readNBT(CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS,registeredPlayers,null,nbt);
        }
    }
}
