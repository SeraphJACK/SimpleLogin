package top.seraphjack.simplelogin.server.storage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.nio.file.Paths;

@OnlyIn(Dist.DEDICATED_SERVER)
public class SLStorage {
    public final StorageProvider storageProvider;
    private static SLStorage INSTANCE;

    public static SLStorage instance() {
        return INSTANCE;
    }

    public static void initialize(String method) {
        if (INSTANCE == null) {
            INSTANCE = new SLStorage(method);
        }
    }

    private SLStorage(String method) {
        // noinspection SwitchStatementWithTooFewBranches
        switch (method) {
            case "file": {
                try {
                    storageProvider = new StorageProviderFile(Paths.get(".", "simplelogin", "storage", "entries.json"));
                } catch (IOException ex) {
                    throw new RuntimeException("Unable to initialize storage provider", ex);
                }
                break;
            }
            default: {
                throw new RuntimeException("Invalid storage method: " + method + ".");
            }
        }
    }
}
