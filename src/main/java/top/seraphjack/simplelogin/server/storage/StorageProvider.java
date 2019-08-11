package top.seraphjack.simplelogin.server.storage;

import net.minecraft.world.GameType;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;

@NotThreadSafe
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
}
