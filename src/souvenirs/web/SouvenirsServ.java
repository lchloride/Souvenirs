package souvenirs.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import user.web.UserManager;

/**
 * Servlet implementation class SouvenirsServ
 */
/*@WebServlet("/SouvenirsServ")*/
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		//If login information is wrong, redirect to index.jsp in order to login in again
		logger.debug(session + " " + session.getAttribute("username")+" "+session.getAttribute("password"));
		if (!UserManager.checkLogin(session.getAttribute("username"), session.getAttribute("password"))) {
			response.sendRedirect("loginfail.jsp");
			return;
		}
		logger.debug("123456123");
		// Forward or Redirect result to assigned page
		String dispatchURL = "homepage.jsp";

			RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
			dispatcher.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
