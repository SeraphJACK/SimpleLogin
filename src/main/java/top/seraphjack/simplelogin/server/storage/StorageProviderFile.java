package top.seraphjack.simplelogin.server.storage;

import com.google.gson.Gson;
import top.seraphjack.simplelogin.SimpleLogin;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

@NotThreadSafe
public class StorageProviderFile implements StorageProvider {
    private Gson gson;
    private Path path;
    private Map<String, String> entries;

    @SuppressWarnings("unchecked")
    public StorageProviderFile(Path path) throws IOException {
        this.path = path;
        this.gson = new Gson();

        if (Files.exists(path)) {
            entries = new HashMap<>(gson.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), Map.class));
        } else {
            entries = new HashMap<>();
        }
    }

    @Override
    public boolean checkPassword(String username, String password) {
        if (entries.containsKey(username)) {
            return entries.get(username).equals(password);
        }
        return false;
    }

    @Override
    public void unregister(String username) {
        entries.remove(username);
    }

    @Override
    public boolean registered(String username) {
        return entries.containsKey(username);
    }

    @Override
    public void register(String username, String password) {
        if (!entries.containsKey(username)) {
            entries.put(username, password);
        }
    }

    @Override
    public void save() throws IOException {
        try {
            Files.write(path, gson.toJson(entries).getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            SimpleLogin.logger.error("Unable to save entries", ex);
            throw ex;
        }
    }
}
