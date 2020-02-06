package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public interface ISLEntry {
    String getPassword();

    void setPassword(String password);

    void setGameType(int gameType);

    int getGameType();
}
