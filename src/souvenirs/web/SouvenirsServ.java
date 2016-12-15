package souvenirs.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import tool.ImageLoader;
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
			// Forward to assigned page
			String dispatchURL = "homepage.jsp";
			List<String> para = new ArrayList<>();
			para.add("user");
			para.add((String)session.getAttribute("user_id"));
			
			request.setAttribute("Avatar" ,ImageLoader.genImageQuery(false, para));
			//request.setAttribute("Avatar", "/SouvenirsData"+(String)DB.execSQLQuery("select avatar from user where username='"+session.getAttribute("username")+"'").get(0).get(0));
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
