package top.seraphjack.simplelogin.server.storage;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.GameType;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;

public abstract class StorageProviderSQL implements StorageProvider {

    protected abstract Connection getSQLConnection();

    @Override
    public boolean checkPassword(String username, String password) {
        try {
            PreparedStatement st = getSQLConnection().prepareStatement("SELECT password FROM sl_entries WHERE username=?");
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
            PreparedStatement st = getSQLConnection().prepareStatement("DELETE FROM sl_entries WHERE username=?");
            st.setString(1, username);
            st.execute();
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error deleting entry", ex);
        }
    }

    @Override
    public boolean registered(String username) {
        try {
            PreparedStatement st = getSQLConnection().prepareStatement("SELECT EXISTS(SELECT * from sl_entries WHERE username=?)");
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
            PreparedStatement st = getSQLConnection().prepareStatement("INSERT INTO sl_entries (username,password,defaultGameType) VALUES (?,?,?)");
            st.setString(1, username);
            st.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            st.setInt(3, SLConfig.server.defaultGameType);
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
            PreparedStatement st = getSQLConnection().prepareStatement("SELECT defaultGameType FROM sl_entries where username=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return null;
            return GameType.getByID(rs.getInt("defaultGameType"));
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error looking up entry", ex);
            return GameType.getByID(SLConfig.server.defaultGameType);
        }
    }

    @Override
    public void setGameType(String username, GameType gameType) {
        try {
            PreparedStatement st = getSQLConnection().prepareStatement("UPDATE sl_entries SET defaultGameType=? WHERE username=?");
            st.setInt(1, gameType.getID());
            st.setString(2, username);
            st.execute();
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error updating entry", ex);
        }
    }

    @Override
    public void changePassword(String username, String newPassword) {
        try {
            PreparedStatement st = getSQLConnection().prepareStatement("UPDATE sl_entries SET password=? WHERE username=?");
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
    public Collection<String> listEntries() {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        try {
            ResultSet rs = getSQLConnection().createStatement().executeQuery("SELECT username FROM sl_entries");
            if (!rs.next()) return Collections.emptySet();
            do {
                builder.add(rs.getString("username"));
            } while (rs.next());
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Error looking up entry", ex);
        }
        return builder.build();
    }
}
