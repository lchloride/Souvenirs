package group.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
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

import souvenirs.web.SouvenirsManager;
import tool.Base64;
import tool.PropertyOper;
import user.User;
import user.web.UserManager;

/**
 * Servlet implementation class GroupServ
 */
//@WebServlet("/GroupServ")
public class GroupServ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(GroupServ.class);
	/**
	 * 上传内存阈值(文件缓冲区的大小)
	 */
	private static final int MEMORY_THRESHOLD = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MEMORY_THRESHOLD"), 16);
	/**
	 * 上传文件的最大大小
	 */
	private static final int MAX_FILE_SIZE = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_FILE_SIZE"), 16);
	/**
	 * 可接收的二进制流的最大大小
	 */
	private static final int MAX_REQUEST_SIZE = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_REQUEST_SIZE"), 16); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GroupServ() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		User login_user = (User) session.getAttribute("user");
		// If login information is wrong, redirect to index.jsp in order to login in again
		if (login_user == null || !UserManager.checkLogin(login_user.getUserId(), login_user.getUsername(), session.getAttribute("password"))) {
			if (login_user!=null&&!session.isNew() )
				logger.warn("User_id, username and password in one session does not match. id=<"
						+ login_user.getUserId() + ">,username=<" + login_user.getUsername()
						+ ">,password=<" + Base64.encode((String) session.getAttribute("password")) + ">");
			session.invalidate();
			response.sendRedirect("loginfail.jsp");
		} else {
			GroupManager gm = GroupManager.getInstance();
			Map<String, String> para = new HashMap<String, String>();
			Enumeration<?> paraNames = request.getParameterNames();

			// Put all parameters from request into a Map transferred to
			// SouvenirManager
			while (paraNames.hasMoreElements()) {
				String paraName = (String) paraNames.nextElement();
				String[] paraValues = request.getParameterValues(paraName);
				String paraValue = paraValues[0];
				if (request.getMethod().contentEquals("GET"))
					para.put(paraName, URLDecoder.decode(paraValue, "UTF-8"));
				else
					para.put(paraName, new String(paraValue.getBytes("iso8859-1"), "UTF-8"));
			}

			// Send user_id as primary key of user to manager object
			para.put("login_user_id",
					session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));

			Map<String, Object> result = new HashMap<>();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

			logger.debug("query_url:<"+query_url+">");
			try {
				if (query_url.contentEquals("group")) {
					result  = gm.displayGroupManager(para);
				} else {
					
					// query_url is wrong
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} catch (Exception e) {
				logger.error("Souvenirs servlet throws an exception", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
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

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String query_url = request.getServletPath();
		query_url = query_url.substring(query_url.lastIndexOf('/') + 1);
		logger.debug("query_url: "+query_url);
		if (query_url.contentEquals("createGroup")) {
			
			HttpSession session = request.getSession(true);
			// If login information is wrong, redirect to index.jsp in order to
			// login in again
			logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
			if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
					session.getAttribute("password"))) {
				session.invalidate();
				response.sendRedirect("loginfail.jsp");
			} else {
				// 检测是否为多媒体上传
				if (!ServletFileUpload.isMultipartContent(request)) {
					// 如果不是则停止
					PrintWriter writer = response.getWriter();
					writer.println("Error: Form type must be 'enctype=multipart/form-data'");
					writer.flush();
					return;
				}

				// 配置上传参数
				DiskFileItemFactory factory = new DiskFileItemFactory();
				// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
				factory.setSizeThreshold(MEMORY_THRESHOLD);
				// 设置临时存储目录
				factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

				ServletFileUpload upload = new ServletFileUpload(factory);

				// 设置最大文件上传值
				upload.setFileSizeMax(MAX_FILE_SIZE);

				// 设置最大请求值 (包含文件和表单数据)
				upload.setSizeMax(MAX_REQUEST_SIZE);

				Map<String, String> para = new HashMap<>();
				Map<String, Object> result = null;
				GroupManager gm = GroupManager.getInstance();

				FileItem file_item = null;
				try {
					// 解析请求的内容提取文件数据
					List<FileItem> formItems = upload.parseRequest(request);
					if (formItems != null && formItems.size() > 0) {
						// 迭代表单数据
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

					// Send parameters and file handler to UploadManager
					result = gm.createGroup(para, file_item);

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
		} else {
			doGet(request, response);
		}
	}

}
