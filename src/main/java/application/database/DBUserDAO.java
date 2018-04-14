package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import application.Main;
import application.exceptions.DatabaseReadException;

public class DBUserDAO {

	public static DBUserDAO instance;
	private String user;
	private Connection c;

	// Queries
	private String login = "INSERT INTO user (USERNAME, ACCESS_TOKEN, ACCESS_SECRET) " + "VALUES (?,?,?)";
	private String count = "SELECT count() FROM user";
	private String users = "SELECT * FROM user";

	private DBUserDAO() {
		c = Main.getDatabaseDAO().getConnection();
	}

	public static DBUserDAO getInstance() {
		if (instance == null) {
			instance = new DBUserDAO();
		}
		return instance;
	}

	public void saveLogin(String username, String token, String tokenSecret) {

		user = username;

		PreparedStatement psmt;
		try {
			psmt = c.prepareStatement(login);
			psmt.setString(1, username);
			psmt.setString(2, token);
			psmt.setString(3, tokenSecret);
			psmt.executeUpdate();
			psmt.close();
		} catch (SQLException e) {
			// FIXME throw new DatabaseWriteException
			e.printStackTrace();
		}

	}

	/**
	 * Check if the user exists in the database to do the fast login
	 * 
	 * @param user
	 * @return
	 * @throws DatabaseReadException 
	 */
	public boolean checkUser(String username) throws DatabaseReadException { // FIXME no estoy convencida de esto ...

		ResultSet rs = null;

		String s = "SELECT username FROM user WHERE username=\"" + username + "\" ";
		try {
			rs = c.createStatement().executeQuery(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (rs != null) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get user data
	 * 
	 * @param query
	 * @param user
	 * @return
	 */
	public String getUserData(String query, String username) throws DatabaseReadException {

		user = username;

		ResultSet rsu;

		String select = "SELECT " + query + " FROM user WHERE username=\"" + user + "\" ";

		try {
			rsu = c.prepareStatement(select).executeQuery();
			return rsu.getString(query);
		} catch (SQLException e) {
			throw new DatabaseReadException();	// TODO poner aqu� una "DataNotFoundException" ?
		}

	}

	public List<String> getUsers() throws DatabaseReadException {

		List<String> u = new Vector<String>();

		ResultSet rsu = null;

		try {
			if (c.createStatement().executeQuery(count).getInt(1) < 1) {
				return null; // FIXME throw new DataNotFoundException
			}
			rsu = c.createStatement().executeQuery(users);
			while (rsu.next()) {
				u.add(rsu.getString("username"));
			}

		} catch (SQLException e) {
			throw new DatabaseReadException();
		}

		return u;
	}

}
