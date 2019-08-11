package top.seraphjack.simplelogin.server.storage;

import java.io.File;
import java.io.IOException;

public class SLStorage {
    public static StorageProvider storageProvider;

    public static void initialize(String method) {
        switch (method) {
            case "file": {
                try {
                    storageProvider = new StorageProviderFile(new File(System.getenv("runDir") + "simplelogin", "entries.json").toPath());
                } catch (IOException ex) {
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
