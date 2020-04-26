package top.seraphjack.simplelogin;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = SimpleLogin.MODID, version = SimpleLogin.VERSION, acceptedMinecraftVersions = "[1.10.2,1.12.2]", acceptableRemoteVersions = "[0.3.5,)")
public class SimpleLogin {
    public static final String MODID = "simplelogin";
    public static final String VERSION = "@VERSION_INJECT@";
    public static Logger logger;

    @SidedProxy(serverSide = "top.seraphjack.simplelogin.server.ServerProxy", clientSide = "top.seraphjack.simplelogin.client.ClientProxy")
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit();
        logger = e.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        proxy.serverStarting(e);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent e) throws IOException {
        proxy.serverStopping(e);
    }

}
