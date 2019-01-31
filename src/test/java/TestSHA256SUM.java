import org.junit.Assert;
import org.junit.Test;
import top.seraphjack.simplelogin.utils.SHA256;

public class TestSHA256SUM {
    @Test
    public void testSHA256Sum() {
        Assert.assertEquals(SHA256.getSHA256("test"), "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
        System.out.println(SHA256.getSHA256("jfdsalkjfoienalvdnlkajfd"));
    }
}
