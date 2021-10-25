package top.seraphjack.simplelogin.server.storage;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.GameType;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class StorageProviderSQL implements StorageProvider {
    private final Connection conn;

    public StorageProviderSQL(Connection conn) {
        this.conn = conn;
        try {
            conn.createStatement()
                    .execute("""
                            CREATE TABLE IF NOT EXISTS sl_entries
                            (
                                username        varchar(32),
                                defaultGameType tinyint,
                                password        varchar(255),
                                PRIMARY KEY (username)
                            )""");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public boolean checkPassword(String username, String password) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    SELECT password
                    FROM sl_entries
                    WHERE username = ?""");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return false;
            return BCrypt.checkpw(password, rs.getString("password"));
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error looking up password", ex);
            return false;
        }
    }

    @Override
    public void unregister(String username) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    DELETE
                    FROM sl_entries
                    WHERE username = ?""");
            st.setString(1, username);
            st.execute();
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error deleting entry", ex);
        }
    }

    @Override
    public boolean registered(String username) {
        try {
            PreparedStatement st = conn.prepareStatement("SELECT EXISTS(SELECT * from sl_entries WHERE username = ?)");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            return rs.next() && rs.getBoolean(1);
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error looking up entry", ex);
            return false;
        }
    }

    @Override
    public void register(String username, String password) {
        if (registered(username)) return;
        try {
            PreparedStatement st = conn.prepareStatement("INSERT INTO sl_entries (username, password, defaultGameType)\n" +
                    "VALUES (?, ?, ?)");
            st.setString(1, username);
            st.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            st.setInt(3, SLConfig.SERVER.defaultGameType.get());
            st.execute();
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error registering entry", ex);
        }
    }

    @Override
    public void save() {
        // NO-OP
    }

    @Override
    public GameType gameType(String username) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    SELECT defaultGameType
                    FROM sl_entries
                    where username = ?""");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return null;
            return GameType.byId(rs.getInt("defaultGameType"));
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error looking up entry", ex);
            return GameType.byId(SLConfig.SERVER.defaultGameType.get());
        }
    }

    @Override
    public void setGameType(String username, GameType gameType) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    UPDATE sl_entries
                    SET defaultGameType=?
                    WHERE username = ?""");
            st.setInt(1, gameType.getId());
            st.setString(2, username);
            st.execute();
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error updating entry", ex);
        }
    }

    @Override
    public void changePassword(String username, String newPassword) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    UPDATE sl_entries
                    SET password=?
                    WHERE username = ?""");
            st.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            st.setString(2, username);
            st.execute();
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error updating entry", ex);
        }
    }

    @Override
    public boolean dirty() {
        // We don't need to save
        return false;
    }

    @Override
    public Collection<String> getAllRegisteredUsername() {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT username\n" +
                    "FROM sl_entries");
            while (rs.next()) {
                builder.add(rs.getString("username"));
            }
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error looking up entry", ex);
        }
        return builder.build();
    }
}
