package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public interface ISLEntry {

    String getPassword();

    void setPassword(String password);

    void setGameType(int gameType);

    int getGameType();
}
