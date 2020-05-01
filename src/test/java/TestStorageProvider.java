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
        provider.save();

        provider = new StorageProviderFile(entryPath);
        Assert.assertTrue(provider.checkPassword("testUser", "testPassword"));
        Assert.assertFalse(provider.checkPassword("testUser", "wrongPassword"));

        Files.deleteIfExists(entryPath);
    }
}
