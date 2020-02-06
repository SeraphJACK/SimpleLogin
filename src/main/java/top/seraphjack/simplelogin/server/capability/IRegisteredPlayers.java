package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

@OnlyIn(Dist.DEDICATED_SERVER)
public interface IRegisteredPlayers {
    Collection<String> getRegisteredPlayers();

    void add(String name);

    void remove(String name);

    boolean contains(String name);
}
