package top.seraphjack.simplelogin.server;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.CommonProxy;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        new CapabilityLoader();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) {
        super.serverStarting(e);
        PlayerLoginHandler.instance();
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent e) {
        super.serverStopping(e);
        PlayerLoginHandler.instance().stop();
    }

    @Override
    public boolean isPhysicalServer() {
        return true;
    }

    @Override
    public boolean isPhysicalClient() {
        return false;
    }
}
