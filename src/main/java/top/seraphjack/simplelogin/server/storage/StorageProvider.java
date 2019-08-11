package top.seraphjack.simplelogin.server.storage;

import java.io.IOException;

public interface StorageProvider {
    boolean checkPassword(String username, String password);

    void unregister(String username);

    boolean registered(String username);

    void register(String username, String password);

    void save() throws IOException;
}
