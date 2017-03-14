package group.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
 * Servlet implementation class GroupAjaxServ
 */
//@WebServlet("/GroupAjaxServ")
public class GroupAjaxServ extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Logger logger  = Logger.getLogger(GroupAjaxServ.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GroupAjaxServ() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		// If login information is wrong, redirect to index.jsp in order to login in again
		if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
				session.getAttribute("password"))) {
			if (!session.isNew())
				logger.warn("User_id, username and password in one session does not match. id=<"
						+ session.getAttribute("user_id") + ">,username=<" + session.getAttribute("username")
						+ ">,password=<" + Base64.encode((String) session.getAttribute("password")) + ">");
			session.invalidate();
			response.sendRedirect("loginfail.jsp");
		} else {
			GroupAjaxManager gam = GroupAjaxManager.getInstance();
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
			String result = new String();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);
			logger.debug("Group Ajax query_url "+query_url);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			try {
				if (query_url.contentEquals("showMyGroup"))
					
					result = gam.showMyGroup(para);

				else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.getStackTrace();
				logger.warn("", e);
				result = e.getMessage();
			}
			//out.println(URLEncoder.encode(result, "UTF-8"));
			out.println(result);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
