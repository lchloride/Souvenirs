package upload.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	// 上传文件存储目录
	private static final String UPLOAD_DIRECTORY = "upload";

	// 上传配置
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private Logger logger = Logger.getLogger(UploadServ.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.debug(request.getMethod());
		String dispatchURL = "upload.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
		dispatcher.forward(request, response);
	}

	/**
	 * 上传数据及保存文件
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
				writer.println("Error: 表单必须包含 enctype=multipart/form-data");
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

			// 构造临时路径来存储上传的文件
			// 这个路径相对当前应用的目录
			String uploadPath = getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;

			// 如果目录不存在则创建
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}

			Map<String, String> para = new HashMap<>();
			Map<String, Object> result = new HashMap<>();
			UploadManager um = UploadManager.getInstance();

			FileItem file_item = null;
			try {
				// 解析请求的内容提取文件数据
				List<FileItem> formItems = upload.parseRequest(request);
				logger.debug("num:"+formItems.size());
				if (formItems != null && formItems.size() > 0) {
					// 迭代表单数据
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

				logger.debug("val="+para);
				result = um.uploadImg();

				// 处理不在表单中的字段
				if (!file_item.isFormField()) {
					String fileName = new File(file_item.getName()).getName();
					String filePath = uploadPath + File.separator + fileName;
					File storeFile = new File(filePath);
					// 在控制台输出文件的上传路径
					System.out.println(filePath);
					// 保存文件到硬盘
					file_item.write(storeFile);
					request.setAttribute("message", "文件上传成功!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				request.setAttribute("message", "错误信息: " + ex.getMessage());
			}

			// 跳转到 message.jsp
			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
		}
	}
}
