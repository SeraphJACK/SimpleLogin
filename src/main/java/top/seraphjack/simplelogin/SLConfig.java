package top.seraphjack.simplelogin;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SLConfig {
    public static class Server {
        public final ForgeConfigSpec.IntValue secs;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> commandNames;

        public final ForgeConfigSpec.ConfigValue<String> storageProvider;

        public final ForgeConfigSpec.IntValue defaultGameType;

        public final ForgeConfigSpec.BooleanValue protectPlayerCoord;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> plugins;

        Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            secs = builder
                    .comment("Login Timeout(s)")
                    .defineInRange("secs", 60, 0, 1200);

            commandNames = builder
                    .comment("Commands in whitelist can be executed before player login.")
                    .defineList("commandNames", Collections.emptyList(), o -> o instanceof String);

            storageProvider = builder
                    .comment("Available storage method: file(json file) / capability(save in player nbt)")
                    .define("storageProvider", "simplelogin:file");

            defaultGameType = builder
                    .comment("Default game type switched after player login")
                    .defineInRange("defaultGameType", 0, 0, 3);

            protectPlayerCoord = builder
                    .comment("Protect player's coordinates")
                    .define("protectPlayerCoord", false);

            plugins = builder
                    .comment("Player login handler plugins to load")
                    .defineList("plugins",
                            Arrays.asList(
                                    "simplelogin:auto_save",
                                    "simplelogin:resend_request",
                                    "simplelogin:restrict_game_type",
                                    "simplelogin:restrict_movement",
                                    "simplelogin:timeout"
                            ),
                            o -> o instanceof String);

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
}
