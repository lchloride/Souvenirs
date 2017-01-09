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
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_REQUEST_SIZE"), 16); // *

	private Logger logger = Logger.getLogger(UploadServ.class);

	/*
	 * ǰ�˷���GET���󣬿������û���һ�δ�ҳ������û��Դ���ķ�ʽ���������� �������������ʽһ�����������¼��ظ�ҳ��
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
	 * �ϴ����ݲ������������ݴ���UploadManager
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
			Map<String, Object> result = new HashMap<>();
			UploadManager um = UploadManager.getInstance();

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
							// �ڱ���������Java���õ�Ĭ���ַ�������GBK������ǰ�˷����Ķ�������δָ���������룬Java��ʹ��Ĭ�ϵ�GBK������
							// ���GBK���ļ����浽�ļ��У����ıض������룬����Ҫ���ļ���ת��UTF-8��
							// ���ڲ�ͬJava���ò�ͬ�����Զ��ļ���ͳһ����һ��ת�룬��֤������
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

			// ��ת�� message.jsp
			getServletContext().getRequestDispatcher("/upload.jsp").forward(request, response);
		}
	}
}
