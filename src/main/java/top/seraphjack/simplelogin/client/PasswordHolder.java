package top.seraphjack.simplelogin.client;

import top.seraphjack.simplelogin.SimpleLogin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class PasswordHolder {
    private static PasswordHolder INSTANCE;

    public static PasswordHolder instance() {
        if (INSTANCE == null) {
            INSTANCE = new PasswordHolder();
        }
        return INSTANCE;
    }

    public static final Path PASSWORD_FILE_PATH = Paths.get(".", ".sl_password");

    private String password = UUID.randomUUID().toString();

    private PasswordHolder() {
        if (Files.exists(PASSWORD_FILE_PATH)) {
            read();
        } else {
            save();
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
            Files.write(PASSWORD_FILE_PATH, password.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            SimpleLogin.logger.error("Failed to save password", e);
        }
    }

    public void set(String o) {
        this.password = o;
        save();
    }

    public String password() {
        return password;
    }
}
