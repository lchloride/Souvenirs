package user.web;

import java.io.IOException;
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

import org.eclipse.jdt.internal.compiler.env.IGenericField;

/**
 * Servlet implementation class UserServ
 */
/* @WebServlet("/UserServ") */
public class UserServ extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServ() {
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
		/*
		 * response.getWriter().append("Served at: "
		 * ).append(request.getContextPath());
		 */
		HttpSession session = request.getSession(true);
		UserManager um = UserManager.getInstance();
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
		System.out.println(session + " " + para);

		para.put("login_username",
				session.getAttribute("username") == null ? "" : (String) session.getAttribute("username"));
		para.put("login_password",
				session.getAttribute("password") == null ? "" : (String) session.getAttribute("password"));
		// Transfer parameters to AnimeManager
		um.setParameter(para);

		Map<String, Object> result = new HashMap<>();

		// Obtain operation
		String query_url = request.getServletPath();
		query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

		if (query_url.contentEquals("register")) {
			result = um.register();
		} else if (query_url.contentEquals("login")) {
			result = um.login();
		} else {
			result.put("DispatchURL", "index.jsp");
		}

		for (Entry<String, Object> entry : result.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
		
		if (result.containsKey("login_username")&&result.containsKey("login_password")) {
			System.out.println(result.get("login_username") + " " + result.get("login_password"));
			session.setAttribute("username", result.get("login_username"));
			session.setAttribute("password", result.get("login_password"));
		}

		// Forward or Redirect result to assigned page
		String dispatchURL = (String) result.get("DispatchURL");
		if (result.containsKey("Redirect") && (boolean) result.get("Redirect")) {
			response.sendRedirect(dispatchURL);
		} else {
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
