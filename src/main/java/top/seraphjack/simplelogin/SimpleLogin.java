package top.seraphjack.simplelogin;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = SLConstants.MODID, version = SLConstants.VERSION, acceptedMinecraftVersions = "[1.10.2,1.12.2]", acceptableRemoteVersions = "[1.0.0-beta,)")
public class SimpleLogin {
    public static final Logger logger = LogManager.getLogger(SLConstants.MODID);

    @SidedProxy(serverSide = "top.seraphjack.simplelogin.server.ServerProxy", clientSide = "top.seraphjack.simplelogin.client.ClientProxy")
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit();
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
