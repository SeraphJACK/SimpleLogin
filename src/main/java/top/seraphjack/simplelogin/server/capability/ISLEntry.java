package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.server.storage.Position;

@SideOnly(Side.SERVER)
public interface ISLEntry {

    String getPassword();

    void setPassword(String password);

    boolean isFirst();

    void setFirst(boolean first);

    void setGameType(int gameType);

    int getGameType();

    Position getLastPosition();

    void setLastPosition(Position pos);

}
