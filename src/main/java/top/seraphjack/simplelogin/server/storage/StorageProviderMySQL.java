package top.seraphjack.simplelogin.server.storage;

import top.seraphjack.simplelogin.SimpleLogin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StorageProviderMySQL extends StorageProviderSQL {

    private final Connection conn;

    static {
        try {
            DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
        } catch (SQLException ex) {
            SimpleLogin.logger.error("Failed to register mysql driver", ex);
        }
    }

    public StorageProviderMySQL(String host, int port, String dbName, String user, String password) {
        String mysqlUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, dbName);
        try {
            conn = DriverManager.getConnection(mysqlUrl, user, password);
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS sl_entries(username varchar(32), defaultGameType tinyint, password varchar(255))");
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to initialize database", ex);
        }
    }

    @Override
    protected Connection getSQLConnection() {
        return conn;
    }
}
