package top.seraphjack.simplelogin.server.capability;

import java.util.Collection;

public interface IRegisteredPlayers {
    Collection<String> getRegisteredPlayers();

    void add(String name);

    void remove(String name);

    boolean contains(String name);
}
