package tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * This class is a tool class only to offer database operations
 * @author Chenghong Li
 * @author Yiran Zhao
 * @version 2.1
 */
public class DB {

	private static Logger logger = Logger.getLogger(DB.class);

	/**
	 * 从DBCP连接池中获取一个数据库连接
	 * @return 数据库连接对象
	 * @throws NamingException 无法解析DBCP配置时抛出的异常
	 * @throws SQLException create失败时抛出的异常
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
	
	/**
	 * 关闭已打开的连接
	 * @param conn 将要关闭的连接
	 * @throws SQLException 无法正常关闭时抛出异常
	 */
	public static void closeConn(Connection conn) throws SQLException {
		conn.close();
	}

	/**
	 * This method executes a SQL sentence and generates a list of query result
	 * of object
	 * 
	 * @param sql query sentence template
	 * 
	 * @param para parameters for template, with appearance order
	 * 
	 * @param method indicating the method of Store interface
	 * 
	 * @param <T> template type, usually referring to a Bean class
	 *  
	 * @return a set of column names, result content
	 * 
	 * @throws Exception exception from database operation or from method.format() method
	 * 
	 * @see tool.Store#format(List)
	 */
	public static <T> List<T> execSQLQuery (String sql, List<String> para, Store<T> method) throws Exception {
		List<T> form = new ArrayList<T>();
		List<String> col_name = new ArrayList<>();

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			if (method == null)
				throw new Exception("Invalid Format Method in DB.java");
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 1; para != null && i <= para.size(); i++)
				ps.setString(i, para.get(i - 1));
			rs = ps.executeQuery();

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
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<" + sql + "> Msg:<" + e.getMessage() + ">", e);
			throw e;
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
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return form;
	}

	/**
	 * This method executes a SQL sentence and generates a list of query result
	 * of object with parameter list of Object type
	 * 
	 * @param sql query sentence template
	 * 
	 * @param para parameters for template, with appearance order
	 * 
	 * @param method indicating the method of Store interface
	 * 
	 * @param <T> template type, usually referring to a Bean class
	 *  
	 * @return a set of column names, result content
	 * 
	 * @throws Exception exception from database operation or from method.format() method
	 * 
	 * @see tool.Store#format(List)
	 */
	public static <T> List<T> execSQLQueryO (String sql, List<Object> para, Store<T> method) throws Exception {
		List<T> form = new ArrayList<T>();
		List<String> col_name = new ArrayList<>();

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			if (method == null)
				throw new Exception("Invalid Format Method in DB.java");
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 1; para != null && i <= para.size(); i++)
				ps.setObject(i, para.get(i - 1));
			rs = ps.executeQuery();

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
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<" + sql + "> Msg:<" + e.getMessage() + ">", e);
			throw e;
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
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return form;
	}
	
	/**
	 * 执行普通sql查询(select)
	 * 
	 * @param sql 查询语句模板
	 * 
	 * @param para 模板参数，依出现序
	 * 
	 * @return 查询结果，以二维List的形式
	 */
	public static List<List<Object>> execSQLQuery(String sql, List<String> para) {
		List<List<Object>> result = new ArrayList<List<Object>>();
		List<Object> result_entry = null;
		Connection conn = null;
		/* Statement stmt = null; */
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 1; para != null && i <= para.size(); i++)
				ps.setString(i, para.get(i - 1));
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				result_entry = new ArrayList<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					result_entry.add(rs.getObject(i));
				result.add(result_entry);
			}
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<" + sql + "> Msg:<" + e.getMessage() + ">", e);
		} finally {
			// TODO: handle finally clause
			try {
				rs.close();
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

	/**
	 * 执行普通sql查询(select)
	 * 
	 * @param sql 查询语句模板
	 * 
	 * @param para 模板参数，依出现序，每个元素的类型为Object
	 * 
	 * @return 查询结果，以二维List的形式
	 */
	public static List<List<Object>> execSQLQueryO(String sql, List<Object> para) {
		List<List<Object>> result = new ArrayList<List<Object>>();
		List<Object> result_entry = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 1; para != null && i <= para.size(); i++)
				ps.setObject(i, para.get(i - 1));
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				result_entry = new ArrayList<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					result_entry.add(rs.getObject(i));
				result.add(result_entry);
			}
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<" + sql + "> Msg:<" + e.getMessage() + ">", e);
		} finally {
			// TODO: handle finally clause
			try {
				rs.close();
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
	
	/**
	 * 执行更新表操作(insert/delete/update)
	 * 
	 * @param sql 变量是要执行的SQL语句模板
	 * 
	 * @param para 按照模板中出现的顺序，存储参数的一个List，类型是String
	 * 
	 * @return 一个整数，操作影响的行数
	 * 
	 * @throws Exception 数据库执行失败时抛出异常
	 */
	public static int execSQLUpdate(String sql, List<String> para) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		int affect_row_count = 0;
		try {
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 1; para != null && i <= para.size(); i++)
				ps.setString(i, para.get(i - 1));
			affect_row_count = ps.executeUpdate();
			logger.debug(affect_row_count);
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<" + sql + "> Msg:<" + e.getMessage() + ">", e);
			throw e;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.warn("Cannot close connection of sql", e);
			}
		}
		return affect_row_count;
	}
	
	/**
	 * 试验用DB获取方法，参数均为object类型
	 * @param sql 变量是要执行的SQL语句模板
	 * @param para 按照模板中出现的顺序，存储参数的一个List，类型是Object
	 * @return 一个整数，操作影响的行数
	 * @throws Exception 数据库执行失败时抛出异常
	 */
	public static int execSQLUpdateO(String sql, List<Object> para) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		int affect_row_count = 0;
		try {
			conn = getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 1; para != null && i <= para.size(); i++)
				ps.setObject(i, para.get(i - 1));
			affect_row_count = ps.executeUpdate();
			logger.debug(affect_row_count);
		} catch (Exception e) {
			logger.warn("exec SQL failed! SQL:<" + sql + "> Msg:<" + e.getMessage() + ">", e);
			throw e;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.warn("Cannot close connection of sql", e);
			}
		}
		return affect_row_count;
	}
}