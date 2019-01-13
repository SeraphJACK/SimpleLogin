package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public interface IPassword {

    public String getPassword();

    public void setPassword(String password);

    public boolean isFirst();

    public void setFirst(boolean first);

}
