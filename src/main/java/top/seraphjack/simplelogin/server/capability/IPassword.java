package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public interface IPassword {

    String getPassword();

    void setPassword(String password);

    boolean isFirst();

    void setFirst(boolean first);

    void setGameType(int gameType);

    int getGameType();

}
