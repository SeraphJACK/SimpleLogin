package top.seraphjack.simplelogin;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SLConfig {
    public static class Server {
        public final ForgeConfigSpec.IntValue secs;

        public final ForgeConfigSpec.ConfigValue<List<String>> commandNames;

        public final ForgeConfigSpec.ConfigValue<String> storageMethod;

        public final ForgeConfigSpec.IntValue defaultGameType;

        Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            secs = builder
                    .comment("Login Timeout(s)")
                    .defineInRange("secs", 60, 0, 1200);

            commandNames = builder
                    .comment("Commands in whitelist can be executed before player login.")
                    .define("commandNames", Collections.emptyList());

            storageMethod = builder
                    .comment("Available storage method: file(json file) / capability(save in player nbt)")
                    .define("storageMethod", "file");

            defaultGameType = builder
                    .comment("Default game type switched after player login")
                    .defineInRange("defaultGameType", 0, 0, 3);

            builder.pop();
        }
    }

    static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<String> password;

        Client(ForgeConfigSpec.Builder builder) {
            builder.push("client");

            password = builder
                    .comment("User password")
                    .define("password", UUID.randomUUID().toString());

            builder.pop();
        }
    }


    static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}
