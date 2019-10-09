package top.seraphjack.simplelogin.server.capability;

import java.util.List;

public interface IRegisteredPlayers {
    List<String> getRegisteredPlayers();

    void add(String name);

    void remove(String name);
}
