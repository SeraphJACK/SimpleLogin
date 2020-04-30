package top.seraphjack.simplelogin.server.storage;

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
                } catch (Exception ex) {
                    throw new RuntimeException("Unable to initialize storage provider", ex);
                }
                break;
            }
            case "capability": {
                storageProvider = new StorageProviderCapability();
                break;
            }
            default: {
                throw new RuntimeException("Invalid storage method: " + method + ".");
            }
        }
    }
}
