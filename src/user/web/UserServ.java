package user.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import tool.PropertyOper;
import user.User;

/**
 * User
 * Servlet���Ʋ㣺�����ǰ̨��ȡ���ݣ�����û�session��¼������url������Ӧ��ҵ�񷽷�����ȡҵ�񷽷����ص����ݲ����͸���Ӧ��ǰ��ҳ�棬
 * �洢�û���¼��Ϣ��session
 * 
 */
/* @WebServlet("/UserServ") */
public class UserServ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserServ.class);
	/**
	 * �ϴ��ڴ���ֵ(�ļ��������Ĵ�С)
	 */
	private static final int MEMORY_THRESHOLD = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MEMORY_THRESHOLD"), 16);
	/**
	 * �ϴ��ļ�������С
	 */
	private static final int MAX_FILE_SIZE = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_FILE_SIZE"), 16);
	/**
	 * �ɽ��յĶ�������������С
	 */
	private static final int MAX_REQUEST_SIZE = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_REQUEST_SIZE"), 16); 
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServ() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@link Servlet#init(ServletConfig)}
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * User Servlet ���Ʋ��ȡ-�ַ��������û��ĵǳ�ֱ����servlet�н��У�û����дҵ�񷽷���
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response) <br>
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		UserManager um = UserManager.getInstance();
		Map<String, String> para = new HashMap<String, String>();
		Enumeration<?> paraNames = request.getParameterNames();

		// Put all parameters from request into a Map transferred to
		// UserManager
		while (paraNames.hasMoreElements()) {
			String paraName = (String) paraNames.nextElement();
			String[] paraValues = request.getParameterValues(paraName);
			String paraValue = paraValues[0];
			para.put(paraName, new String(paraValue.getBytes("iso8859-1"), "UTF-8"));
		}
		System.out.println(session + " " + para);

		para.put("login_username",
				session.getAttribute("username") == null ? "" : (String) session.getAttribute("username"));
		para.put("login_user_id",
				session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));
		para.put("login_password",
				session.getAttribute("password") == null ? "" : (String) session.getAttribute("password"));
		para.put("login_verifycode_name", session.getAttribute("login_verifycode_name") == null ? ""
				: (String) session.getAttribute("login_verifycode_name"));
		para.put("register_verifycode_name", session.getAttribute("register_verifycode_name") == null ? ""
				: (String) session.getAttribute("register_verifycode_name"));

		Map<String, Object> result = new HashMap<>();

		// Obtain operation url
		String query_url = request.getServletPath();
		query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

		// Call specific method according to query_url
		if (query_url.contentEquals("register")) {
			// Register a new user
			result = um.register(para);
		} else if (query_url.contentEquals("login")) {
			// Check validation of user login information
			result = um.login(para);
		} else if (query_url.contentEquals("setting")) {
			// Check validation of user login information
			result = um.displaySetting(para);
		}  else if (query_url.contentEquals("logout")) {
			// Let session invalidate
			session.invalidate();
			logger.info("User(id=<" + para.get("login_user_id") + ">) logout.");
			response.sendRedirect("index.jsp");
			return;
		} else {
			// Invalid query_url, redirect to index.jsp
			result.put("DispatchURL", "index.jsp");
		}

		for (Entry<String, Object> entry : result.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}

		// If login succeeded, put user information into session
		if (result.containsKey("login_user_id") && result.containsKey("login_username")
				&& result.containsKey("login_password")) {
			session.setAttribute("username", result.get("login_username"));
			session.setAttribute("password", result.get("login_password"));
			session.setAttribute("user_id", result.get("login_user_id"));
			session.setAttribute("user", result.get("login_user"));
		}

		// Store verify code in session
		if (result.containsKey("VerifyCode"))
			session.setAttribute("login_verifycode_name", result.get("VerifyCode"));

		// Forward or Redirect result to assigned page
		String dispatchURL = (String) result.get("DispatchURL");
		if (result.containsKey("Redirect") && (boolean) result.get("Redirect")) {
			response.sendRedirect(dispatchURL);
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * ����doGet����
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Obtain operation url
		String query_url = request.getServletPath();
		query_url = query_url.substring(query_url.lastIndexOf('/') + 1);
		if (query_url.contentEquals("updateSettings")){
			
			HttpSession session = request.getSession(true);
			// If login information is wrong, redirect to index.jsp in order to
			// login in again
			logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
			if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
					session.getAttribute("password"))) {
				session.invalidate();
				response.sendRedirect("loginfail.jsp");
			} else {
				// ����Ƿ�Ϊ��ý���ϴ�
				if (!ServletFileUpload.isMultipartContent(request)) {
					// ���������ֹͣ
					PrintWriter writer = response.getWriter();
					writer.println("Error: Form type must be 'enctype=multipart/form-data'");
					writer.flush();
					return;
				}

				// �����ϴ�����
				DiskFileItemFactory factory = new DiskFileItemFactory();
				// �����ڴ��ٽ�ֵ - �����󽫲�����ʱ�ļ����洢����ʱĿ¼��
				factory.setSizeThreshold(MEMORY_THRESHOLD);
				// ������ʱ�洢Ŀ¼
				factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

				ServletFileUpload upload = new ServletFileUpload(factory);

				// ��������ļ��ϴ�ֵ
				upload.setFileSizeMax(MAX_FILE_SIZE);

				// �����������ֵ (�����ļ��ͱ�����)
				upload.setSizeMax(MAX_REQUEST_SIZE);

				Map<String, String> para = new HashMap<>();
				Map<String, Object> result = null;
				UserManager um = UserManager.getInstance();

				FileItem file_item = null;
				try {
					// ���������������ȡ�ļ�����
					List<FileItem> formItems = upload.parseRequest(request);
					if (formItems != null && formItems.size() > 0) {
						// ����������
						for (FileItem item : formItems) {
							if (item.isFormField()) {
								String key = item.getFieldName();
								String val = new String(item.getString().getBytes("ISO-8859-1"), "UTF-8");
								para.put(key, val);
							} else {
								// Store file handler
								file_item = item;
							}
						}
					}
					// Send user_id as primary key of user to manager object
					para.put("login_user_id",
							session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));
					para.put("login_password",
							session.getAttribute("password") == null ? "" : (String) session.getAttribute("password"));
					// Send parameters and file handler to UploadManager
					result =um.updateSettings(para, file_item);
					
					if (result.containsKey("new_password")) {
						session.setAttribute("password", result.get("new_password"));
					}
					
					if (result.containsKey("new_username")) {
						session.setAttribute("username", result.get("new_username"));
						((User)session.getAttribute("user")).setUsername((String)result.get("new_username"));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					request.setAttribute("Error", ex.getMessage());
				}

				for (Entry<String, Object> entry : result.entrySet()) {
					request.setAttribute(entry.getKey(), entry.getValue());
				}
				// Forward or redirect to assigned page
				String dispatchURL = result.containsKey("DispatchURL") ? (String) result.get("DispatchURL") : "index.jsp";
				if (result.containsKey("Is_redirect"))
					response.sendRedirect(dispatchURL);
				else {
					RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
					dispatcher.forward(request, response);
				}
			}
		} else
			doGet(request, response);
	}

}
