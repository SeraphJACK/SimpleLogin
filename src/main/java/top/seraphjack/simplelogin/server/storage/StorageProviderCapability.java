package top.seraphjack.simplelogin.server.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.IRegisteredPlayers;
import top.seraphjack.simplelogin.server.capability.ISLEntry;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@ThreadSafe
public class StorageProviderCapability implements StorageProvider {
    @Nonnull
    private IRegisteredPlayers registeredPlayers = Objects.requireNonNull(FMLCommonHandler.instance()
            .getMinecraftServerInstance().getWorld(0)
            .getCapability(CapabilityLoader.CAPABILITY_REGISTERED_PLAYERS, null));

    @Override
    public boolean checkPassword(String username, String password) {
        if (isPlayerOnline(username)) {
            return BCrypt.checkpw(password, getEntry(username).getPassword());
        }
        return false;
    }

    @Override
    public void unregister(String username) {
        registeredPlayers.remove(username);
    }

    @Override
    public boolean registered(String username) {
        return registeredPlayers.getRegisteredPlayers().contains(username);
    }

    @Override
    public void register(String username, String password) {
        getEntry(username).setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        registeredPlayers.add(username);
    }

    @Override
    public void save() {
        // NO-OP
    }

    @Override
    public GameType gameType(String username) {
        if (isPlayerOnline(username)) {
            return GameType.getByID(getEntry(username).getGameType());
        }
        return GameType.getByID(SLConfig.server.defaultGameType);
    }

    @Override
    public void setGameType(String username, GameType gameType) {
        if (isPlayerOnline(username)) {
            getEntry(username).setGameType(gameType.getID());
        }
    }

    @Override
    public void changePassword(String username, String newPassword) {
        if (isPlayerOnline(username)) {
            getEntry(username).setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }
    }

    @Override
    public boolean dirty() {
        return false;
    }

    @Override
    public Collection<String> getAllRegisteredUsername() {
        return Arrays.asList(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOnlinePlayerNames());
    }

    private boolean isPlayerOnline(String id) {
        return Arrays.asList(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOnlinePlayerNames()).contains(id);
    }

    @Nonnull
    private ISLEntry getEntry(String id) {
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(id);
        assert player != null;
        return Objects.requireNonNull(player.getCapability(CapabilityLoader.CAPABILITY_SL_ENTRY, null));
    }
}
