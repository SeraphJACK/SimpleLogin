import net.minecraft.world.GameType;
import org.junit.Assert;
import org.junit.Test;
import top.seraphjack.simplelogin.server.storage.StorageProvider;
import top.seraphjack.simplelogin.server.storage.StorageProviderFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestStorageProvider {
    @Test
    public void testStorageProviderFile() throws Throwable {
        Path entryPath = Paths.get(".", "testEntries.json");

        Files.deleteIfExists(entryPath);

        StorageProvider provider = new StorageProviderFile(entryPath);
        Assert.assertFalse(provider.registered("testUser"));

        provider.register("testUser", "testPassword");
        Assert.assertTrue(provider.registered("testUser"));
        Assert.assertEquals(provider.listEntries().size(), 1);
        Assert.assertEquals(provider.listEntries().iterator().next(), "testUser");
        Assert.assertTrue(provider.checkPassword("testUser", "testPassword"));
        Assert.assertFalse(provider.checkPassword("testUser", "wrongPassword"));
        Assert.assertFalse(provider.checkPassword("wrongUser", "testPassword"));
        Assert.assertTrue(provider.dirty());
        provider.save();
        Assert.assertFalse(provider.dirty());

        provider = new StorageProviderFile(entryPath);
        Assert.assertTrue(provider.checkPassword("testUser", "testPassword"));
        Assert.assertFalse(provider.checkPassword("testUser", "wrongPassword"));

        provider.changePassword("testUser", "newPassword");
        Assert.assertTrue(provider.checkPassword("testUser", "newPassword"));
        Assert.assertFalse(provider.checkPassword("testUSer", "testPassword"));

        Assert.assertEquals(provider.gameType("testUser"), GameType.SURVIVAL);
        provider.setGameType("testUser", GameType.CREATIVE);
        Assert.assertEquals(provider.gameType("testUser"), GameType.CREATIVE);

        provider.unregister("testUser");
        Assert.assertFalse(provider.registered("testUser"));

        Files.deleteIfExists(entryPath);
    }
}
