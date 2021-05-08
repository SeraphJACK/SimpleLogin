package top.seraphjack.simplelogin.server.storage;

import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SLConstants;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class StorageProviderSQLite extends StorageProviderSQL {
    public StorageProviderSQLite() throws SQLException {
        // Default path at $WORLD_DIR/sl_entries.dat
        super(DriverManager.getConnection("jdbc:sqlite:" +
                ServerLifecycleHooks.getCurrentServer().func_240776_a_(SLConstants.SL_ENTRY)));
    }
}
