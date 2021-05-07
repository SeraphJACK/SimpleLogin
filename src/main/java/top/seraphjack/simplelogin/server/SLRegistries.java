package top.seraphjack.simplelogin.server;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.handler.HandlerPlugin;
import top.seraphjack.simplelogin.server.handler.plugins.*;
import top.seraphjack.simplelogin.server.storage.StorageProvider;
import top.seraphjack.simplelogin.server.storage.StorageProviderFile;
import top.seraphjack.simplelogin.server.storage.StorageProviderSQLite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class SLRegistries<S> {
    private final Map<ResourceLocation, Supplier<? extends S>> plugins = new HashMap<>();

    public void register(ResourceLocation rl, Supplier<? extends S> plugin) {
        if (plugins.containsKey(rl)) {
            throw new IllegalArgumentException("Resource location " + rl.toString() + " already exists.");
        }
        plugins.put(rl, plugin);
    }

    public Optional<Supplier<? extends S>> get(ResourceLocation rl) {
        return Optional.ofNullable(plugins.get(rl));
    }

    private SLRegistries() {
    }

    public static final SLRegistries<HandlerPlugin> PLUGINS = new SLRegistries<>();
    public static final SLRegistries<StorageProvider> STORAGE_PROVIDERS = new SLRegistries<>();

    static {
        // Default plugins
        PLUGINS.register(new ResourceLocation("simplelogin", "auto_save"), AutoSave::new);
        PLUGINS.register(new ResourceLocation("simplelogin", "protect_coord"), ProtectCoord::new);
        PLUGINS.register(new ResourceLocation("simplelogin", "resend_request"), ResendRequest::new);
        PLUGINS.register(new ResourceLocation("simplelogin", "restrict_game_type"), RestrictGameType::new);
        PLUGINS.register(new ResourceLocation("simplelogin", "restrict_movement"), RestrictMovement::new);
        PLUGINS.register(new ResourceLocation("simplelogin", "timeout"), Timeout::new);

        // Default storage providers
        STORAGE_PROVIDERS.register(new ResourceLocation("simplelogin", "file"),
                () -> mustCall(() -> new StorageProviderFile(ServerLifecycleHooks.getCurrentServer().func_240776_a_(SLConstants.SL_ENTRY))));
        STORAGE_PROVIDERS.register(new ResourceLocation("simplelogin", "sqlite"),
                () -> mustCall((Callable<StorageProvider>) StorageProviderSQLite::new));
    }

    private static <S> S mustCall(Callable<S> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
