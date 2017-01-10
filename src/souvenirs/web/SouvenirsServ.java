package souvenirs.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import tool.Base64;
import user.web.UserManager;

/**
	 * Souvenirs Servlet控制层：负责从前台收取数据，检查用户session登录，根据url调用相应的业务方法，收取业务方法返回的数据并发送给对应的前端页面
 */
/* @WebServlet("/SouvenirsServ") */
public class SouvenirsServ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SouvenirsServ.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SouvenirsServ() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * 控制层获取-分发的方法
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		// If login information is wrong, redirect to index.jsp in order to
		// login in again
		if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
				session.getAttribute("password"))) {
			if (!session.isNew())
				logger.warn("User_id, username and password in one session does not match. id=<"
						+ session.getAttribute("user_id") + ">,username=<" + session.getAttribute("username")
						+ ">,password=<" + Base64.encode((String) session.getAttribute("password")) + ">");
			session.invalidate();
			response.sendRedirect("loginfail.jsp");
		} else {
			SouvenirsManager sm = SouvenirsManager.getInstance();
			Map<String, String> para = new HashMap<String, String>();
			Enumeration<?> paraNames = request.getParameterNames();

			// Put all parameters from request into a Map transferred to
			// SouvenirManager
			while (paraNames.hasMoreElements()) {
				String paraName = (String) paraNames.nextElement();
				String[] paraValues = request.getParameterValues(paraName);
				String paraValue = paraValues[0];
				para.put(paraName, new String(paraValue.getBytes("iso8859-1"), "UTF-8"));
			}

			// Send user_id as primary key of user to manager object
			para.put("login_user_id",
					session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));

			logger.debug(para);
			Map<String, Object> result = new HashMap<>();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

			// Select and call specific function according query_url
			if (query_url.contentEquals("homepage")) {
				// Display Content when firstly open the page
				result = sm.displayContent(para);
			} else if (query_url.contentEquals("formPicture")) {
				// Covert base64 code of image to bit flow and send it back to
				// front side as an attachment
				// This method is called when downloading souvenir
				String content = request.getParameter("picture");
				logger.debug(content==null);
				String content_base64 = content.substring(content.indexOf("base64,") + 7, content.length());
				byte[] rs_byte = Base64.decodeBytes(content_base64);
				logger.debug("Base64 code length:" + content_base64.length() + ", Image bit length:" + rs_byte.length);
				logger.info("User(id=<" + session.getAttribute("user_id")
						+ ">) made a souvenir, original base64 code size:<" + content_base64.length() + ">");
				try {
					OutputStream os = null;

					response.reset();
					response.setCharacterEncoding("UTF-8");
					// Set MIME type as image
					response.setContentType("image/*");
					// Set content as attachment and bit flow
					response.setHeader("Content-Disposition", "attachment;filename=img.png");
					// response.setHeader("Content-Disposition","inline;filename=img.jpg");
					// Set content length
					response.setContentLength(rs_byte.length);

					os = response.getOutputStream();
					os.write(rs_byte);
					os.flush();
					os.close();
					logger.info("User(id=<" + session.getAttribute("user_id") + ">) making souvenir succeeded.");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					logger.info("User(id=<" + session.getAttribute("user_id") + ">) making souvenir failed. ErrorMsg:<"
							+ e.getMessage() + ">");
				}
				return;
			} else if (query_url.contentEquals("making")) {
				// Display making page when firstly open making page
				result = sm.makingSouvenirs(para);
			} else if (query_url.contains("AlbumAjax")) {
				// Send json string of album info when front side open an Ajax
				// query
				logger.info("User(id=<" + session.getAttribute("user_id") + ">) query image address in album <"
						+ para.get("album_name") + ">.");
				String rs = sm.getImageAddrInAlbum(para);
				response.setContentType("text/xml; charset=UTF-8");
				// Let browser not to store cache
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				try {
					PrintWriter out = response.getWriter();
					out.write(rs);
					out.close();
					logger.info("User(id=<" + session.getAttribute("user_id")
							+ ">) obtained json string of image address in album <" + para.get("album_name") + ">");
				} catch (Exception e) {
					// TODO: handle exception
				}
				return;
			} else {
				// query_url is wrong
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			for (Entry<String, Object> entry : result.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			// Forward to assigned page
			String dispatchURL = result.containsKey("DispatchURL") ? (String) result.get("DispatchURL") : "index.jsp";
			RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * 调用doGet方法
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
