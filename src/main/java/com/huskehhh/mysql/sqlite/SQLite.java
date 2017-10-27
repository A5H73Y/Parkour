package com.huskehhh.mysql.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.huskehhh.mysql.Database;
import me.A5H73Y.Parkour.Parkour;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLite extends Database {
	private final String dbLocation;

	/**
	 * Creates a new SQLite instance
	 *
	 * @param pluginFolder Location of the Database (Must end in .db)
	 */
	public SQLite(String pluginFolder) {
		this.dbLocation = pluginFolder;
	}

	@Override
	public Connection openConnection() {
		if (checkConnection()) {
			return connection;
		}

		try {
            String pathOverride = Parkour.getPlugin().getConfig().getString("SQLite.PathOverride");
            String path = pathOverride.isEmpty() ? dbLocation + File.separator + "sqlite-db" : pathOverride;

            File dataFolder = new File(path);
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File databaseFile = new File(dataFolder, "parkour.db");
            if (!databaseFile.exists()) {
                try {
                    databaseFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("Unable to create database: " + e.getMessage());
                }
            }
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }
            return connection;

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }

		return null;
	}
}