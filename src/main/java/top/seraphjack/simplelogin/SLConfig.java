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
        @Config.Name("超时时间(秒)")
        public int secs = 60;
    }

    public static class Client {
        @Config.Name("密码")
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
