package top.seraphjack.simplelogin.server.storage;

import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Collection;

@ThreadSafe
@OnlyIn(Dist.DEDICATED_SERVER)
public interface StorageProvider {
    boolean checkPassword(String username, String password);

    void unregister(String username);

    boolean registered(String username);

    void register(String username, String password);

    void save() throws IOException;

    GameType gameType(String username);

    void setGameType(String username, GameType gameType);

    void changePassword(String username, String newPassword);

    boolean dirty();

    /**
     * Should be immutable
     *
     * @return all registered username
     */
    Collection<String> getAllRegisteredUsername();
}
