package top.seraphjack.simplelogin.server.storage;

import top.seraphjack.simplelogin.SimpleLogin;

import java.io.IOException;
import java.nio.file.Paths;

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
        switch (method) {
            case "file": {
                try {
                    storageProvider = new StorageProviderFile(Paths.get(".", "simplelogin", "storage", "entries.json"));
                } catch (IOException ex) {
                    throw new RuntimeException("Unable to initialize storage provider", ex);
                }
                break;
            }
            case "capability": {
                storageProvider = new StorageProviderCapability();
                SimpleLogin.logger.warn("Capability storage is deprecated, consider switch to file.");
                break;
            }
            default: {
                throw new RuntimeException("Invalid storage method: " + method + ".");
            }
        }
    }
}
