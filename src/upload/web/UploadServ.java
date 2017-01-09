package upload.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
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
import user.web.UserManager;

/**
 * Servlet implementation class UploadServlet
 */
// @WebServlet("/UploadServlet")
public class UploadServ extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Properties of uploading
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
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_REQUEST_SIZE"), 16); // *

	private Logger logger = Logger.getLogger(UploadServ.class);

	/*
	 * 前端发来GET请求，可能是用户第一次打开页面或者用户以错误的方式发送了数据 这两种情况处理方式一样，都是重新加载该页面
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		// If login information is wrong, redirect to index.jsp in order to
		// login in again
		logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
		// Check login status
		if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
				session.getAttribute("password"))) {
			// Auto login failed, redirect to loginfail.jsp
			session.invalidate();
			response.sendRedirect("loginfail.jsp");
		} else {
			UploadManager um = new UploadManager();
			Map<String, String> para = new HashMap<>();
			Map<String, Object> result = new HashMap<>();
			// Send user_id as primary key of user to manager object
			para.put("login_user_id",
					session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));

			// um.setParameter(para);
			result = um.displayContent(para);

			// Put every parameters back to front side
			for (Entry<String, Object> entry : result.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			String dispatchURL = "upload.jsp";
			RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * 上传数据并解析，将数据传给UploadManager
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
			Map<String, Object> result = new HashMap<>();
			UploadManager um = UploadManager.getInstance();

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
							// 在本服务器中Java设置的默认字符编码是GBK，由于前端发来的二进制流未指定解析编码，Java会使用默认的GBK来编码
							// 如果GBK的文件名存到文件中，中文必定会乱码，所以要将文件名转成UTF-8。
							// 由于不同Java配置不同，所以对文件名统一进行一次转码，保证无乱码
							para.put("origin_filename", new String(
									item.getName().getBytes(System.getProperty("sun.jnu.encoding")), "UTF-8"));
						}
					}
				}
				// Send user_id as primary key of user to manager object
				para.put("login_user_id",
						session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));

				// Send parameters and file handler to UploadManager
				result = um.uploadPicture(para, file_item);

				for (Entry<String, Object> entry : result.entrySet()) {
					request.setAttribute(entry.getKey(), entry.getValue());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				request.setAttribute("Upload_result", false);
				request.setAttribute("Error_msg", "Msg: " + ex.getMessage());
			}

			// 跳转到 message.jsp
			getServletContext().getRequestDispatcher("/upload.jsp").forward(request, response);
		}
	}
}
