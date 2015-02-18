package com.drevelopment.amplechatbot.bukkit.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.drevelopment.amplechatbot.api.database.DatabaseHandler;
import com.drevelopment.amplechatbot.bukkit.BukkitPlugin;
import com.drevelopment.amplechatbot.bukkit.database.options.DatabaseOptions;
import com.drevelopment.amplechatbot.bukkit.database.options.MySQLOptions;
import com.drevelopment.amplechatbot.bukkit.database.options.SQLiteOptions;

public class SQLDatabaseHandler implements DatabaseHandler {

	private DatabaseOptions dop;
	private Connection conn;

	public SQLDatabaseHandler (BukkitPlugin plugin, DatabaseOptions dop) {
		this.dop = dop;
		plugin.getDataFolder().mkdirs();

		if (dop instanceof SQLiteOptions) {
			try {
				((SQLiteOptions) dop).getSQLFile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public DatabaseOptions getDatabaseOptions() {
		return dop;
	}

	public Connection getConnection() {
		return conn;
	}

	public boolean open() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			conn = null;
			return false;
		}
		if (dop instanceof MySQLOptions){
			this.conn = DriverManager.getConnection("jdbc:mysql://"+((MySQLOptions) dop).getHostname()+":"+
					((MySQLOptions) dop).getPort()+"/"+
					((MySQLOptions) dop).getDatabase(),
					((MySQLOptions) dop).getUsername(),
					((MySQLOptions) dop).getPassword());
			return true;
		}
		else if (dop instanceof SQLiteOptions) {
			this.conn = DriverManager.getConnection("jdbc:sqlite:"+((SQLiteOptions) dop).getSQLFile().getAbsolutePath());
			return true;
		} else {
			return false;
		}
	}

	public void close() throws SQLException {
		conn.close();
	}

	public boolean reload() {
		try {
			close();
			return open();
		} catch (SQLException e) {
			return false;
		}
	}

	public ResultSet query(String query) throws SQLException {
		Statement st = null;
		ResultSet rs = null;

		st = conn.createStatement();
		if (query.toLowerCase().contains("delete") || query.toLowerCase().contains("update") || query.toLowerCase().contains("insert")) {
			st.executeUpdate(query);
			return rs;
		} else {
			rs = st.executeQuery(query);
			return rs;
		}
	}

	public boolean createTable(String table) throws SQLException {
		Statement st = conn.createStatement();
		return st.execute(table);
	}

	public int currentEpoch() {
		try {
			if (dop instanceof MySQLOptions) {
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("SELECT UNIX_TIMESTAMP(now());");
				rs.first();
				return rs.getInt(1);
			} else if (dop instanceof SQLiteOptions) {
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("SELECT strftime('%s','now');");
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	public String escapeQuotes(String str) {
		if (str != null) {
			String target = "\"";
			String replacement = "\"\"";
			str = str.replaceAll(target, replacement);
			target = "'";
			replacement = "''";
			str = str.replaceAll(target, replacement);
		}
		return str;
	}

	public String unescapeQuotes(String str) {
		if (str != null) {
			String target = "\"\"";
			String replacement = "\"";
			str = str.replaceAll(target, replacement);
			target = "''";
			replacement = "'";
			str = str.replaceAll(target, replacement);
		}
		return str;
	}

	@Override
	public String getDatabaseType() {
		if (dop instanceof MySQLOptions) {
			return "MySQL";
		} else
		if (dop instanceof SQLiteOptions) {
			return "SQLite";
		}
		return "None";
	}
}
