package top.seraphjack.simplelogin.server.storage;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.nio.file.Path;

@OnlyIn(Dist.DEDICATED_SERVER)
public class SLStorage {
    public final StorageProvider storageProvider;
    private static SLStorage INSTANCE;

    public static SLStorage instance() {
        return INSTANCE;
    }

    public static void initialize(String method, Path path) {
        if (INSTANCE == null) {
            INSTANCE = new SLStorage(method, path);
        }
    }

    private SLStorage(String method, Path path) {
        // noinspection SwitchStatementWithTooFewBranches
        switch (method) {
            case "file": {
                try {
                    storageProvider = new StorageProviderFile(path);
                } catch (IOException ex) {
                    throw new ReportedException(new CrashReport("Unable to initialize storage provider", ex));
                }
                break;
            }
            default: {
                throw new RuntimeException("Invalid storage method: " + method + ".");
            }
        }
    }
}
