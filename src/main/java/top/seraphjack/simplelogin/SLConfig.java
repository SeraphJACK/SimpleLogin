package top.seraphjack.simplelogin;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Config(modid = SLConstants.MODID)
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

        @Config.Name("Storage method")
        @Config.Comment("Available storage method: file(json file) / capability(save in player nbt)")
        public String storageMethod = "file";

        @Config.Name("Default Game Type")
        @Config.Comment("Default game type switched after player login")
        public int defaultGameType = 0;

        @Config.Name("Enable Command Login Mode")
        @Config.Comment("Ask players to login via command instead of password stored in configuration file")
        public boolean enableCommandLoginMode = false;

        @Config.Name("Protect Player Coordinate")
        @Config.Comment("Teleport players to the word's spawn point when they log out in order to protect their coordinates, " +
                "might be incompatible with some mods (such as compact machine, " +
                "in which case the player would die if they log out in the compact machine world)")
        public boolean protectPlayerCoordinate = false;

        @Config.Name("MySQL")
        public MySQL mySql = new MySQL();
    }

    public static class Client {
        @Config.Name("Password")
        public String password = UUID.randomUUID().toString();

        @Config.Name("UseConfigPasswordInsteadOfCommandLogin")
        @Config.Comment("Use password stored in configuration to register/login even if the server has enabled command login mode")
        public boolean useConfigPasswordInsteadOfCommandLogin = false;

        @Config.Name("StorePasswordExternally")
        @Config.Comment("Storage password aside from the configuration file, in minecraft_folder/sl_password.txt")
        public boolean storePasswordExternally = true;
    }

    public static class MySQL {
        @Config.Name("User")
        public String user = "simplelogin";

        @Config.Name("Password")
        public String password = "";

        @Config.Name("Host")
        public String host = "localhost";

        @Config.Name("Port")
        public int port = 3306;

        @Config.Name("DB Name")
        public String dbName = "simplelogin";
    }

    @Mod.EventBusSubscriber(modid = SLConstants.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(SLConstants.MODID)) {
                ConfigManager.sync(SLConstants.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
