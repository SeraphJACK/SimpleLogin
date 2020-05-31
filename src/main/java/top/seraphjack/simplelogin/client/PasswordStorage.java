package top.seraphjack.simplelogin.client;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@SideOnly(Side.CLIENT)
public class PasswordStorage {
    private static boolean storeExternally;
    public static final Path externalPath = Paths.get("sl_password.txt");

    public static void init() {
        storeExternally = SLConfig.client.storePasswordExternally;
    }

    public static String getPassword() {
        if (storeExternally) {
            try {
                if (!Files.exists(externalPath)) {
                    Files.createFile(externalPath);
                    Files.write(externalPath, SLConfig.client.password.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
                    return SLConfig.client.password;
                } else {
                    return new String(Files.readAllBytes(externalPath), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return SLConfig.client.password;
        }
    }

    public static void changePassword(String to) {
        if (storeExternally) {
            try {
                Files.write(externalPath, to.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            SLConfig.client.password = to;
            ConfigManager.sync(SLConstants.MODID, Config.Type.INSTANCE);
        }
    }
}
