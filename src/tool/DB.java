package tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.sun.crypto.provider.RSACipher;

import sun.util.logging.resources.logging;


/*
 * This class is a tool class only to offer database operations
 */
public class DB {
	/*
	 * This method will obtain an available connection and return it.
	 */
	public static Connection getConn() throws NamingException, SQLException {
		Connection conn = null;
		Context ctx = new InitialContext();
		// 参数java:/comp/env为固定路径
		Context envContext = (Context) ctx.lookup("java:/comp/env");
		// 参数jdbc/mysqlds为数据源和JNDI绑定的名字
		DataSource ds = (DataSource) envContext.lookup("jdbc/mysqlds");
		conn = ds.getConnection();
		// System.out.println(conn.toString() + "<span
		// style='color:red;'>JNDI测试成功<span>");

		return conn;
	}

	public static void closeConn(Connection conn) throws SQLException {
		conn.close();
	}

	/*
	 * This method executes a SQL sentence and generates a list of query result
	 * of object
	 * 
	 * @param sql is query sentence
	 * 
	 * @param method indicates the method of Store interface
	 * 
	 * @return a set of column names, result content
	 */
	public static <T> Object[] execSQLQuery(String sql, Store<T> method) {
		List<T> form = new ArrayList<T>();
		List<String> col_name = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (method == null)
				throw new Exception("Invalid Format Method in DB.java");
			conn = getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				col_name.add(rsmd.getColumnName(i));

			while (rs.next()) {
				List<Object> item = new ArrayList<>();
				T item_obj;
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					item.add(rs.getObject(i));
				item_obj = method.format(item);
				form.add(item_obj);
			}
			// System.out.println("querysize:"+form.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO: handle finally clause
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Object[] result = new Object[2];
		result[0] = col_name;
		result[1] = form;
		return result;
	}

	// 普通查表
	public static List<List<Object>> execSQLQuery(String sql) {
		List<List<Object>> result = new ArrayList<List<Object>>();
		List<Object>result_entry = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				result_entry = new ArrayList<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					result_entry.add(rs.getObject(i));
				result.add(result_entry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO: handle finally clause
			try {
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	// 更新表
	public static int execSQLUpdate(String sql) {
		Connection conn = null;
		Statement stmt = null;
		int rs = 0;
		try {
			conn = getConn();
			stmt = conn.createStatement();
			rs = stmt.executeUpdate(sql);
		} catch (Exception e) {
			System.out.print("SQL:"+sql);
			e.printStackTrace();
			rs = -1;
		} finally {
			// TODO: handle finally clause
			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rs;
	}
	
	public static String parsePara(String para) {
		String rs = new String();
		rs = para.replaceAll("[\']", "\\\\\'");
		return rs;
	}
}
