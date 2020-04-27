import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

public class TestStorageProvider {
    @Test
    public void testStorageProviderFile() {
        /*
        StorageProvider provider = new StorageProviderFile(Paths.get(".", "testEntries.json"));
        provider.unregister("testUser");
        provider.register("testUser", "testPassword");
        Assert.assertTrue(provider.checkPassword("testUser", "testPassword"));
        Assert.assertFalse(provider.checkPassword("testUser", "wrongPassword"));
        Assert.assertFalse(provider.checkPassword("wrongUser", "testPassword"));
        provider.save();
         */
        System.out.println(BCrypt.gensalt(13));
    }
}
