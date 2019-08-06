package top.seraphjack.simplelogin;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Config(modid = SimpleLogin.MODID)
public class SLConfig {

    @Config.Name("Server")
    public static Server server = new Server();

    @Config.Name("Client")
    public static Client client = new Client();

    public static class Server {
        @Config.Name("Login Timeout(s)")
        public int secs = 60;

        @Config.Name("Whitelisted commands")
        @Config.Comment("Commands in whitelist can be executed before player login.")
        public String[] commandNames = {};

        @Config.Name("Resend request")
        @Config.Comment("Whether should the server resend password request to client")
        public boolean resendCommand = false;
    }

    public static class Client {
        @Config.Name("Password")
        public String password = UUID.randomUUID().toString();
    }

    @Mod.EventBusSubscriber(modid = SimpleLogin.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(SimpleLogin.MODID)) {
                ConfigManager.sync(SimpleLogin.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
