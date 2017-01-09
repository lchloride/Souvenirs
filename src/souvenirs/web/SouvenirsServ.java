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
		// If login information is wrong, redirect to index.jsp in order to login in again
		logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
		if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
				session.getAttribute("password"))) {
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

			//sm.setParameter(para);

			Map<String, Object> result = new HashMap<>();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

			//Select and call specific function according query_url
			if (query_url.contentEquals("homepage")) {
				//Display Content when firstly open the page
				result = sm.displayContent(para);
			} else if (query_url.contentEquals("logout")) {
				//Let session invalidate
				session.invalidate();
				response.sendRedirect("index.jsp");
				return;
			} else if (query_url.contentEquals("formPicture")) {
				//Covert base64 code of image to bit flow and send it back to front side as an attachment
				//This method is called when downloading souvenir
				String content = request.getParameter("picture");
				String content_base64 = content.substring(content.indexOf("base64,") + 7,
						content.length());
				byte[] rs_byte = Base64.decodeBytes(content_base64);
				logger.debug("Base64 code length:"+content_base64.length() + ", Image bit length:" + rs_byte.length);
				try {
					OutputStream os = null;

					response.reset();
					response.setCharacterEncoding("UTF-8");
					// Set MIME type as image
					response.setContentType("image/*");
					// Set content as attachment and bit flow
					response.setHeader("Content-Disposition", "attachment;filename=img.png");
					// response.setHeader("Content-Disposition",
					// "inline;filename=img.jpg");
					//Set content length
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
			} else if (query_url.contentEquals("making")) {
				//Display making page when firstly open making page
				result = sm.makingSouvenirs(para);
			} else if (query_url.contains("AlbumAjax")) {
				//Send json string of album info when front side open an Ajax query
				String rs = sm.getImageAddrInAlbum(para);
				response.setContentType("text/xml; charset=UTF-8");
				// 以下两句为取消在本地的缓存
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				PrintWriter out = response.getWriter();
				out.write(rs);// 注意这里向jsp输出的流，在script中的截获方法
				out.close();
				return;
			} else {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
