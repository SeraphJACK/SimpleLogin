package top.seraphjack.simplelogin.server.storage;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.GameType;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;

import javax.activation.UnsupportedDataTypeException;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class StorageProviderFile implements StorageProvider {
    public static final String STORAGE_VERSION = "1.1";

    private final Gson gson;
    private final Path path;
    private final Map<String, POJOUserEntry> entries;
    private boolean dirty = false;

    public StorageProviderFile(Path path) throws IOException {
        this.path = path;
        this.gson = new Gson();
        entries = new ConcurrentHashMap<>();

        if (Files.exists(path)) {
            JsonObject json = gson.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), JsonObject.class);
            if (json.getAsJsonPrimitive("version").getAsString().equals(STORAGE_VERSION)) {
                JsonArray entries = json.getAsJsonArray("entries");
                for (JsonElement element : entries) {
                    String username = element.getAsJsonObject().getAsJsonPrimitive("username").getAsString();
                    String password = element.getAsJsonObject().getAsJsonPrimitive("password").getAsString();
                    int gameType = element.getAsJsonObject().getAsJsonPrimitive("gameType").getAsInt();
                    POJOUserEntry userEntry = new POJOUserEntry();
                    userEntry.username = username;
                    userEntry.password = password;
                    userEntry.gameType = gameType;
                    this.entries.put(username, userEntry);
                }
            } else {
                throw new UnsupportedDataTypeException("Storage version doesn't match");
            }
        } else {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
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
        synchronized (this) {
            try {
                JsonObject jsonObject = new JsonObject();
                JsonArray entries = new JsonArray();
                for (POJOUserEntry entry : this.entries.values()) {
                    JsonObject jsonEntry = new JsonObject();
                    jsonEntry.addProperty("username", entry.username);
                    jsonEntry.addProperty("password", entry.password);
                    jsonEntry.addProperty("gameType", entry.gameType);
                    entries.add(jsonEntry);
                }
                jsonObject.addProperty("version", STORAGE_VERSION);
                jsonObject.add("entries", entries);
                Files.write(path, gson.toJson(jsonObject).getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
                dirty = false;
            } catch (IOException ex) {
                SimpleLogin.logger.error("Unable to save entries", ex);
                throw ex;
            }
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
            entries.get(username).password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        }
    }

    @Override
    public boolean dirty() {
        return dirty;
    }

    @Override
    public Collection<String> listEntries() {
        return new ImmutableList.Builder<String>().addAll(entries.keySet()).build();
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
