package group.web;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import souvenirs.web.SouvenirsManager;
import tool.Base64;
import tool.exception.BadRequestException;
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
		doGet(request, response);
	}

}
