package top.seraphjack.simplelogin.server.storage;

import com.google.gson.Gson;
import net.minecraft.world.GameType;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@NotThreadSafe
public class StorageProviderFile implements StorageProvider {
    private Gson gson;
    private Path path;
    private Map<String, POJOUserEntry> entries;
    private boolean dirty = false;

    public StorageProviderFile(Path path) throws IOException {
        this.path = path;
        this.gson = new Gson();

        if (Files.exists(path)) {
            entries = new HashMap<>();
            Arrays.stream(gson.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), POJOUserEntry[].class)).forEach(e -> entries.put(e.username, e));
        } else {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            entries = new HashMap<>();
            Files.createFile(path);
        }
    }

    @Override
    public boolean checkPassword(String username, String password) {
        if (entries.containsKey(username)) {
            return BCrypt.checkpw(password, entries.get(username).password);
        }
        return false;
    }

    @Override
    public void unregister(String username) {
        dirty = true;
        entries.remove(username);
    }

    @Override
    public boolean registered(String username) {
        return entries.containsKey(username);
    }

    @Override
    public void register(String username, String password) {
        if (!entries.containsKey(username)) {
            entries.put(username, newEntry(username, BCrypt.hashpw(password, BCrypt.gensalt())));
            dirty = true;
        }
    }

    @Override
    public void save() throws IOException {
        try {
            Files.write(path, gson.toJson(entries.values().toArray()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            dirty = false;
        } catch (IOException ex) {
            SimpleLogin.logger.error("Unable to save entries", ex);
            throw ex;
        }
    }

    @Override
    public GameType gameType(String username) {
        return GameType.getByID(entries.get(username).gameType);
    }

    @Override
    public void setGameType(String username, GameType gameType) {
        if (entries.containsKey(username)) {
            dirty = true;
            entries.get(username).gameType = gameType.getID();
        }
    }

    @Override
    public void changePassword(String username, String newPassword) {
        if (entries.containsKey(username)) {
            dirty = true;
            entries.get(username).password = newPassword;
        }
    }

    @Override
    public boolean dirty() {
        return dirty;
    }

    private POJOUserEntry newEntry(String username, String password) {
        POJOUserEntry entry = new POJOUserEntry();
        entry.username = username;
        entry.password = password;
        entry.gameType = SLConfig.server.defaultGameType;
        return entry;
    }

    private static class POJOUserEntry {
        public String password, username;
        public int gameType;
    }
}
