package tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


/*
 * This class is a tool class only to offer database operations
 */
public class DB {
	/*
	 * This method will obtain an available connection and return it.
	 */
	private static Logger logger = Logger.getLogger(DB.class);
	
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
	public static <T> Object[] execSQLQuery(String sql, List<String> para, Store<T> method) {
		List<T> form = new ArrayList<T>();
		List<String> col_name = new ArrayList<>();

		Connection conn = null;
		/*Statement stmt = null;*/
		ResultSet rs = null;
		PreparedStatement ps=null;
		
		try {
			if (method == null)
				throw new Exception("Invalid Format Method in DB.java");
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i=1; para!=null && i<=para.size(); i++)
				ps.setString(i, para.get(i-1));
			rs = ps.executeQuery();
			/*stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);*/

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
			logger.warn("exec SQL failed! SQL:<"+sql+"> Msg:<"+e.getMessage()+">", e);
		} finally {
			// TODO: handle finally clause
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
/*			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
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
	public static List<List<Object>> execSQLQuery(String sql, List<String> para) {
		List<List<Object>> result = new ArrayList<List<Object>>();
		List<Object>result_entry = null;
		Connection conn = null;
		/*Statement stmt = null;*/
		ResultSet rs = null;
		PreparedStatement ps=null;
		try {
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i=1; para!=null&&i<=para.size(); i++)
				ps.setString(i, para.get(i-1));
			rs = ps.executeQuery();
/*			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);*/

			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				result_entry = new ArrayList<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					result_entry.add(rs.getObject(i));
				result.add(result_entry);
			}
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<"+sql+"> Msg:<"+e.getMessage()+">", e);
		} finally {
			// TODO: handle finally clause
			try {
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
/*			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
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
	public static Map<String, Object> execSQLUpdate(String sql, List<String>para) {
		Map<String, Object> rs = new HashMap<>();
		Connection conn = null;
		/*Statement stmt = null;*/
		PreparedStatement ps = null;
		int affect_row_count = 0;
		try {
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i=1; para!=null && i<=para.size(); i++)
				ps.setString(i, para.get(i-1));
/*			stmt = conn.createStatement();
			affect_row_count = stmt.executeUpdate(sql);*/
			affect_row_count = ps.executeUpdate();
			rs.put("process_state", true);
			rs.put("affect_row_count", affect_row_count);
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<"+sql+"> Msg:<"+e.getMessage()+">", e);
			rs.put("process_state", false);
			rs.put("error_msg", e.getMessage());			
		} finally {
			// TODO: handle finally clause
/*			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				logger.warn("Cannot close statement of sql", e1);
			}*/
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.warn("Cannot close connection of sql", e);
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
