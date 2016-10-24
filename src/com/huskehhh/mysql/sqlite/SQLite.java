package com.huskehhh.mysql.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.huskehhh.mysql.Database;

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

		File dataFolder = new File(dbLocation + File.separator + "sqlite-db/");
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}

		File databaseFile = new File(dataFolder + File.separator + "parkour.db");
		if (!(databaseFile.exists())) {
			try {
				databaseFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Unable to create database!");
			}
		}

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	@Override
	public String getType(){
		return "SQLite";
	}
}