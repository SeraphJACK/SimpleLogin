package top.seraphjack.simplelogin.server.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.ISLEntry;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

@ThreadSafe
@Deprecated
public class StorageProviderCapability implements StorageProvider {

    private List<String> resetPasswordList;

    public StorageProviderCapability() {
        resetPasswordList = new LinkedList<>();
    }

    @Override
    public boolean checkPassword(String username, String password) {
        if (isPlayerOnline(username)) {
            return BCrypt.checkpw(password, getEntry(username).getPassword());
        }
        return false;
    }

    @Override
    public void unregister(String username) {
        if (isPlayerOnline(username)) {
            getEntry(username).setFirst(true);
        } else {
            resetPasswordList.add(username);
        }
    }

    @Override
    public boolean registered(String username) {
        if (isPlayerOnline(username)) {
            return !(getEntry(username).isFirst() || resetPasswordList.contains(username));
        }
        return !resetPasswordList.contains(username);
    }

    @Override
    public void register(String username, String password) {
        getEntry(username).setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        getEntry(username).setFirst(false);
        resetPasswordList.remove(username);
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
    public void setLastPosition(String username, Position pos) {
        if (isPlayerOnline(username)) {
            getEntry(username).setLastPosition(pos);
        }
    }

    @Override
    public Position getLastPosition(String username) {
        if (isPlayerOnline(username)) {
            return getEntry(username).getLastPosition();
        }
        return SLConstants.defaultPosition;
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
        return Objects.requireNonNull(player.getCapability(CapabilityLoader.CAPABILITY_PASSWORD, null));
    }
}
