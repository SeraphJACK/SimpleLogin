package top.seraphjack.simplelogin.server.storage;

import top.seraphjack.simplelogin.SimpleLogin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StorageProviderMySQL extends StorageProviderSQL {

    static {
        try {
            DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Failed to register mysql driver", ex);
        }
    }

    private final String mysqlUrl;
    private final String user;
    private final String password;

    public StorageProviderMySQL(String host, int port, String dbName, String user, String password) {
        this.mysqlUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, dbName);
        this.user = user;
        this.password = password;
        try {
            getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS sl_entries(username varchar(16), defaultGameType tinyint, password varchar(16))");
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to initialize database", ex);
        }
    }

    @Override
    protected Connection getSQLConnection() {
        try {
            return DriverManager.getConnection(mysqlUrl, user, password);
        } catch (SQLException ex) {
            throw new RuntimeException("Cannot connect to database", ex);
        }
    }
}
