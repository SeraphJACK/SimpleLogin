package top.seraphjack.simplelogin;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(SimpleLogin.MODID)
public class SimpleLogin {
    public static final String MODID = "simplelogin";
    public static Logger logger = LogManager.getLogger(MODID);

    public SimpleLogin() {
        // TODO
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e) {
        // TODO
    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent e) throws IOException {
        // TODO
    }

}
