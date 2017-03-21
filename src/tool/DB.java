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
	 * ��DBCP���ӳ��л�ȡһ�����ݿ�����
	 * @return ���ݿ����Ӷ���
	 * @throws NamingException �޷�����DBCP����ʱ�׳����쳣
	 * @throws SQLException createʧ��ʱ�׳����쳣
	 */
	public static Connection getConn() throws NamingException, SQLException {
		Connection conn = null;
		Context ctx = new InitialContext();
		// ����java:/comp/envΪ�̶�·��
		Context envContext = (Context) ctx.lookup("java:/comp/env");
		// ����jdbc/mysqldsΪ����Դ��JNDI�󶨵�����
		DataSource ds = (DataSource) envContext.lookup("jdbc/mysqlds");
		conn = ds.getConnection();
		// System.out.println(conn.toString() + "<span
		// style='color:red;'>JNDI���Գɹ�<span>");

		return conn;
	}
	
	/**
	 * �ر��Ѵ򿪵�����
	 * @param conn ��Ҫ�رյ�����
	 * @throws SQLException �޷������ر�ʱ�׳��쳣
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
	 * ִ����ͨsql��ѯ(select)
	 * 
	 * @param sql ��ѯ���ģ��
	 * 
	 * @param para ģ���������������
	 * 
	 * @return ��ѯ������Զ�άList����ʽ
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
	 * ִ����ͨsql��ѯ(select)
	 * 
	 * @param sql ��ѯ���ģ��
	 * 
	 * @param para ģ���������������ÿ��Ԫ�ص�����ΪObject
	 * 
	 * @return ��ѯ������Զ�άList����ʽ
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
	 * ִ�и��±����(insert/delete/update)
	 * 
	 * @param sql ������Ҫִ�е�SQL���ģ��
	 * 
	 * @param para ����ģ���г��ֵ�˳�򣬴洢������һ��List��������String
	 * 
	 * @return һ������������Ӱ�������
	 * 
	 * @throws Exception ���ݿ�ִ��ʧ��ʱ�׳��쳣
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
	 * ������DB��ȡ������������Ϊobject����
	 * @param sql ������Ҫִ�е�SQL���ģ��
	 * @param para ����ģ���г��ֵ�˳�򣬴洢������һ��List��������Object
	 * @return һ������������Ӱ�������
	 * @throws Exception ���ݿ�ִ��ʧ��ʱ�׳��쳣
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