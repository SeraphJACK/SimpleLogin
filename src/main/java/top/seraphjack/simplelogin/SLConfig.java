package top.seraphjack.simplelogin;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SLConfig {
    public static class Server {
        public final ForgeConfigSpec.IntValue secs;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistCommands;

        public final ForgeConfigSpec.ConfigValue<String> storageProvider;

        public final ForgeConfigSpec.IntValue defaultGameType;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> plugins;

        Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            secs = builder
                    .comment("Login Timeout(s)")
                    .defineInRange("secs", 600, 0, 1200);

            whitelistCommands = builder
                    .comment("Commands in whitelist can be executed before player login.")
                    .defineList("commandNames", Collections.emptyList(), o -> o instanceof String);

            storageProvider = builder
                    .comment("Which storage provider to use")
                    .comment("Simplelogin provides to available providers by default:")
                    .comment("simplelogin:file -> file based storage")
                    .comment("simplelogin:sqlite -> sqlite based storage")
                    .comment("Note that you need to add JDBC-sqlite yourself if you want to use sqlite")
                    .define("storageProvider", "simplelogin:file");

            defaultGameType = builder
                    .comment("Default game type switched after player login")
                    .comment("0,1,2,3 represents survival,creative,adventure,spectator")
                    .defineInRange("defaultGameType", 0, 0, 3);

            plugins = builder
                    .comment("Player login handler plugins to load")
                    .comment("simplelogin:protect_coord is disabled by default, add to here to enable coord protect feature")
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
