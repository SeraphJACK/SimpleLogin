package top.seraphjack.simplelogin;

import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

public abstract class CommonProxy {
    @OverridingMethodsMustInvokeSuper
    public void preInit() {

    }

    @OverridingMethodsMustInvokeSuper
    public void init() {
    }

    @OverridingMethodsMustInvokeSuper
    public void serverStarting(FMLServerStartingEvent e) {

    }

    @OverridingMethodsMustInvokeSuper
    public void serverStopping(FMLServerStoppingEvent e) throws IOException {

    }
}
