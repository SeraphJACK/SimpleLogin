package top.seraphjack.simplelogin.server.storage;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.seraphjack.simplelogin.server.SLRegistries;

@OnlyIn(Dist.DEDICATED_SERVER)
public class SLStorage {
    public final StorageProvider storageProvider;
    private static SLStorage INSTANCE;

    public static SLStorage instance() {
        return INSTANCE;
    }

    public static void initialize(String provider) {
        if (INSTANCE == null) {
            INSTANCE = new SLStorage(provider);
        }
    }

    private SLStorage(String provider) {
        storageProvider = SLRegistries.STORAGE_PROVIDERS.get(new ResourceLocation(provider))
                .orElseThrow(() -> new RuntimeException("Storage provider not found: " + provider))
                .get();
    }
}
