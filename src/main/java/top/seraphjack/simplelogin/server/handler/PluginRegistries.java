package top.seraphjack.simplelogin.server.handler;

import net.minecraft.util.ResourceLocation;
import top.seraphjack.simplelogin.server.handler.plugins.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class PluginRegistries {
    private static final Map<ResourceLocation, Supplier<? extends HandlerPlugin>> plugins = new HashMap<>();

    public static void register(ResourceLocation rl, Supplier<? extends HandlerPlugin> plugin) {
        if (plugins.containsKey(rl)) {
            throw new IllegalArgumentException("Resource location " + rl.toString() + " already exists.");
        }
        plugins.put(rl, plugin);
    }

    static Optional<Supplier<? extends HandlerPlugin>> getPlugin(ResourceLocation rl) {
        return Optional.ofNullable(plugins.get(rl));
    }

    static {
        // Default plugins
        register(new ResourceLocation("simplelogin", "auto_save"), AutoSave::new);
        register(new ResourceLocation("simplelogin", "protect_coord"), ProtectCoord::new);
        register(new ResourceLocation("simplelogin", "resend_request"), ResendRequest::new);
        register(new ResourceLocation("simplelogin", "restrict_game_type"), RestrictGameType::new);
        register(new ResourceLocation("simplelogin", "restrict_movement"), RestrictMovement::new);
        register(new ResourceLocation("simplelogin", "timeout"), Timeout::new);
    }
}
