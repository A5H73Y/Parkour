package com.huskehhh.mysql.mysql;

import com.huskehhh.mysql.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to and uses a MySQL database.
 *
 * @author -_Husky_-
 * @author tips48
 */
public class MySQL extends Database {

	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	/**
	 * Creates a new MySQL instance for a specific database.
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param database
	 *            Database name
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySQL(final String hostname, final String port, final String database,
				 final String username, final String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	@Override
	public final Connection openConnection() {
		if (checkConnection()) {
			return getConnection();
		}

		String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port;
		if (database != null) {
			connectionURL += "/" + this.database;
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			setConnection(DriverManager.getConnection(connectionURL, this.user, this.password));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return getConnection();
	}
}
