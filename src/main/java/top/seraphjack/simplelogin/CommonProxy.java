package top.seraphjack.simplelogin;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

public abstract class CommonProxy {
    @OverridingMethodsMustInvokeSuper
    public void preInit() {

    }

    @OverridingMethodsMustInvokeSuper
    public void init() {
        new NetworkLoader();
    }

    @OverridingMethodsMustInvokeSuper
    public void serverStarting(FMLServerStartingEvent e) {

    }

    @OverridingMethodsMustInvokeSuper
    public void serverStopping(FMLServerStoppingEvent e) throws IOException {

    }
}
