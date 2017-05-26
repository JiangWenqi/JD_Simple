package cn.edu.bistu.cs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {

	private final String driver = ConfigReader.getConfig("MYSQL_DEIVER");
	private final String url = ConfigReader.getConfig("MYSQL_URL");
	private final String user = ConfigReader.getConfig("MYSQL_USER");
	private final String password = ConfigReader.getConfig("MYSQL_PASSWORD");
	private static Connection conn;
	private static Statement stmt;

	public JDBC() {
		try {
			// 加载驱动
			Class.forName(driver);
			// 连接数据库
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println("The database connection is successful!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 插入语句
	public void insert(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 查询语句
	public ResultSet select(String sql) {
		try {
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		try {

			conn.close();
			System.out.println("Close the database connection successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JDBC jdbc = new JDBC();
		jdbc.insert("INSERT INTO cellphoneInfo(id,name) VALUES('123', '小米5'),('1234','小米6')");
		jdbc.close();
	}
}