package top.seraphjack.simplelogin.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.seraphjack.simplelogin.SimpleLogin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@OnlyIn(Dist.CLIENT)
public final class PasswordHolder {
    private static PasswordHolder INSTANCE;

    public static PasswordHolder instance() {
        if (INSTANCE == null) {
            INSTANCE = new PasswordHolder();
        }
        return INSTANCE;
    }

    public static final Path PASSWORD_FILE_PATH = Paths.get(".", ".sl_password");

    private String password = null;
    private String pendingPassword = null;
    private boolean initialized = false;

    private PasswordHolder() {
        if (Files.exists(PASSWORD_FILE_PATH)) {
            initialized = true;
            read();
        }
    }

    private void read() {
        try {
            password = new String(Files.readAllBytes(PASSWORD_FILE_PATH), StandardCharsets.UTF_8);
        } catch (IOException e) {
            SimpleLogin.logger.error("Failed to load password", e);
        }
    }

    private void save() {
        try {
            Files.write(PASSWORD_FILE_PATH, password.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            SimpleLogin.logger.error("Failed to save password", e);
        }
    }

    public boolean initialized() {
        return initialized;
    }

    public void initialize(String password) {
        if (initialized) throw new IllegalStateException();
        initialized = true;
        this.password = password;
        save();
    }

    public void setPendingPassword(String o) {
        if (!initialized) throw new IllegalStateException();
        this.pendingPassword = o;
        save();
    }

    public void applyPending() {
        if (!initialized) throw new IllegalStateException();
        if (this.pendingPassword == null) return;
        this.password = pendingPassword;
        save();
        this.pendingPassword = null;
    }

    public void dropPending() {
        if (!initialized) throw new IllegalStateException();
        this.pendingPassword = null;
    }

    public String password() {
        if (!initialized) throw new IllegalStateException();
        return password;
    }
}
