package souvenirs.web;

import java.io.IOException;
import java.io.OutputStream;
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
 * Servlet implementation class SouvenirsServ
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

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		// If login information is wrong, redirect to index.jsp in order to
		// login in again
		logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
		if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"), session.getAttribute("password"))) {
			response.sendRedirect("loginfail.jsp");
		} else {
			SouvenirsManager sm = SouvenirsManager.getInstance();
			Map<String, String> para = new HashMap<String, String>();
			Enumeration<?> paraNames = request.getParameterNames();

			// Put all parameters from request into a Map transferred to
			// AnimeManager
			while (paraNames.hasMoreElements()) {
				String paraName = (String) paraNames.nextElement();
				String[] paraValues = request.getParameterValues(paraName);
				String paraValue = paraValues[0];
				para.put(paraName, new String(paraValue.getBytes("iso8859-1"), "UTF-8"));
			}
			
			para.put("login_user_id",
					session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));
			
			sm.setParameter(para);
			
			Map<String, Object> result = new HashMap<>();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

			if (query_url.contentEquals("homepage")) {
				result = sm.displayContent();
			} else if (query_url.contentEquals("logout")) {
				session.invalidate();
				response.sendRedirect("index.jsp");
				return;
			} else if (query_url.contentEquals("changeImage")){
				String content = request.getParameter("image");
				String content_base64 = content.substring(content.indexOf("base64,")+7, content.length()-(918-858));
				byte[] rs_byte = Base64.decodeBytes(content_base64);
				logger.debug(content_base64.length()+" "+rs_byte.length);
				try {
					OutputStream os = null;

					response.reset();
					response.setCharacterEncoding("UTF-8");
					// 不同类型的文件对应不同的MIME类型
					response.setContentType("image/*");
					// 文件以流的方式发送到客户端浏览器
					response.setHeader("Content-Disposition","attachment;filename=img.jpg");
					// response.setHeader("Content-Disposition", "inline;filename=img.jpg");

					response.setContentLength(rs_byte.length);

					os = response.getOutputStream();
					os.write(rs_byte);
					os.flush();
					os.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return;
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			for (Entry<String, Object> entry : result.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			// Forward to assigned page
			String dispatchURL = result.containsKey("DispatchURL")?(String)result.get("DispatchURL"):"index.jsp";
			RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
