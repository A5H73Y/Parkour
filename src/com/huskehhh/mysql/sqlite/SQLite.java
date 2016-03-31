package com.huskehhh.mysql.sqlite;

import com.huskehhh.mysql.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
     * @param dbLocation Location of the Database (Must end in .db)
     */
    public SQLite(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    @Override
    public Connection openConnection() throws SQLException,
            ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }

        File file = new File(dbLocation);
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Unable to create database!");
            }
        }
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager
                .getConnection("jdbc:sqlite:"
                        + dbLocation);
        return connection;
    }
    
    public void setupTables(){
		try {
			updateSQL("CREATE TABLE IF NOT EXISTS `times` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `course` varchar(15) NOT NULL, `player` varchar(20) NOT NULL, `time` decimal(13,0) NOT NULL, `deaths` int(5) NOT NULL)");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @Override
    public String getType(){
    	return "SQLite";
    }
}