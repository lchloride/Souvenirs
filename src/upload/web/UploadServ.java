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

import user.web.UserManager;

/**
 * Servlet implementation class UploadServlet
 */
// @WebServlet("/UploadServlet")
public class UploadServ extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// �ϴ��ļ��洢Ŀ¼
	private static final String UPLOAD_DIRECTORY = "upload";

	// �ϴ�����
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private Logger logger = Logger.getLogger(UploadServ.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		// If login information is wrong, redirect to index.jsp in order to
		// login in again
		logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
		if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
				session.getAttribute("password"))) {
			session.invalidate();
			response.sendRedirect("loginfail.jsp");
		} else {
			UploadManager um = new UploadManager();
			Map<String, String> para = new HashMap<>();
			Map<String, Object> result = new HashMap<>();
			// Send user_id as primary key of user to manager object
			para.put("login_user_id",
					session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));
			um.setParameter(para);
			result = um.diaplayContent();
			for (Entry<String, Object> entry : result.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			String dispatchURL = "upload.jsp";
			RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * �ϴ����ݼ������ļ�
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
				writer.println("Error: ��������� enctype=multipart/form-data");
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

			// ������ʱ·�����洢�ϴ����ļ�
			// ���·����Ե�ǰӦ�õ�Ŀ¼
			String uploadPath = getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;

			// ���Ŀ¼�������򴴽�
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}

			Map<String, String> para = new HashMap<>();
			Map<String, Object> result = new HashMap<>();
			UploadManager um = UploadManager.getInstance();

			FileItem file_item = null;
			try {
				// ���������������ȡ�ļ�����
				List<FileItem> formItems = upload.parseRequest(request);
				logger.debug("num:" + formItems.size());
				if (formItems != null && formItems.size() > 0) {
					// ����������
					for (FileItem item : formItems) {
						if (item.isFormField()) {
							String key = item.getFieldName();
							String val = item.getString();

							para.put(key, val);
						} else {
							file_item = item;
						}
					}
				}
				// Send user_id as primary key of user to manager object
				para.put("login_user_id",
						session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));

				logger.debug("val=" + para);
				um.setParameter(para);
				result = um.uploadImg();

				// �����ڱ��е��ֶ�
				if (!file_item.isFormField()) {
					String fileName = new File(file_item.getName()).getName();
					String filePath = uploadPath + File.separator + fileName;
					File storeFile = new File(filePath);
					// �ڿ���̨����ļ����ϴ�·��
					System.out.println(filePath);
					// �����ļ���Ӳ��
					file_item.write(storeFile);
					request.setAttribute("message", "�ļ��ϴ��ɹ�!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				request.setAttribute("message", "������Ϣ: " + ex.getMessage());
			}

			// ��ת�� message.jsp
			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
		}
	}
}
