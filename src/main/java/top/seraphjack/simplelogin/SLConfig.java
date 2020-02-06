package top.seraphjack.simplelogin;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.FORGEMOD;

public class SLConfig {
    public static class Server {
        public final ForgeConfigSpec.IntValue secs;

        public final ForgeConfigSpec.ConfigValue<String[]> commandNames;

        public final ForgeConfigSpec.ConfigValue<String> storageMethod;

        public final ForgeConfigSpec.IntValue defaultGameType;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server settings")
                    .push("server");

            secs = builder
                    .comment("Login Timeout(s)")
                    .defineInRange("secs", 60, 0, 1200);

            commandNames = builder
                    .comment("Commands in whitelist can be executed before player login.")
                    .define("commandNames", new String[0]);

            storageMethod = builder
                    .comment("Available storage method: file(json file) / capability(save in player nbt)")
                    .define("storageMethod", "");

            defaultGameType = builder
                    .comment("Default game type switched after player login")
                    .defineInRange("defaultGameType", 0, 0, 3);

            builder.pop();
        }
    }

    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<String> password;

        Client(ForgeConfigSpec.Builder builder) {
            builder
                    .comment("Client settings")
                    .push("client");

            password = builder
                    .comment("User password")
                    .define("password", UUID.randomUUID().toString());

            builder.pop();
        }
    }


    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
    }
}
