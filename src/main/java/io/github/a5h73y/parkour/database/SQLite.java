package io.github.a5h73y.parkour.database;

import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import pro.husk.Database;

/**
 * Connects to and uses a SQLite database.
 *
 * @author tips48
 * @author A5H73Y
 */
public class SQLite extends Database {

    private final String dbLocation;
    private final String dbName;

    /**
     * Creates a new SQLite instance.
     *
     * @param dbLocation Location of the Database
     * @param dbName File name (must end with .db)
     */
    public SQLite(String dbLocation, String dbName) {
        this.dbLocation = dbLocation;
        this.dbName = dbName;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = createConnection();
        }

        return this.connection;
    }

    private Connection createConnection() throws SQLException {
        File dbLocationFolder = new File(dbLocation);

        if (!dbLocationFolder.exists()) {
            dbLocationFolder.mkdirs();
        }

        File dbFile = new File(dbLocation, dbName);

        if (!dbFile.exists()) {
            try {
                dbLocationFolder.createNewFile();
            } catch (IOException e) {
                PluginUtils.log("Unable to create database: " + e.getMessage(), 2);
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            PluginUtils.log("Unable to load SQLite class: " + e.getMessage(), 2);
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
    }
}
