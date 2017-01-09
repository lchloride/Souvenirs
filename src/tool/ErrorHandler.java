package tool;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class ErrorHandler
 */
// @WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ErrorHandler.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrorHandler() {
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
	 *      response) <br>
	 *      如果程序出现异常，将调用ErrorHandler显示友好的错误页面
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}
		String description = "";
		String des_img_path = "";
		if (throwable == null && statusCode == null) {
			description = "Error Message is Missing";
		} else if (statusCode != null) {
			switch (statusCode) {
			case 404:
				des_img_path = "/Souvenirs/res/image/404_not_found.png";
				description = "Sorry, the page you requested has been moved or deleted. ";
				break;
			case 403:
				des_img_path = "/Souvenirs/res/image/403_forbidden.png";
				description = "Sorry, your request was forbidden by server. ";
				break;
			case 500:
				des_img_path = "/Souvenirs/res/image/500_internal_error.png";
				description = "Sorry, the server encountered a problem that it cannot response. ";
				logger.error("ErrorHandler--Description: " + description + ", ErrorCode: " + statusCode + ", Request URI: "
						+ requestUri);
				break;
			default:
				break;
			}
		} else {
			description = "Error Mssage--Servlet Name : " + servletName + "<br/>";
			description += "Exception Type : " + throwable.getClass().getName() + "</br>";
			description += "Request URI: " + requestUri + "<br>";
			description += "Exception Message: " + throwable.getMessage();
			logger.error("ErrorHandler--Description: " + description);
		}

		request.setAttribute("Description", description);
		request.setAttribute("DesImage", des_img_path);
		request.setAttribute("ErrorCode", statusCode);
		request.setAttribute("LastPage", request.getHeader("Referer"));
		RequestDispatcher dispatcher = request.getRequestDispatcher("ErrorPage.jsp");
		dispatcher.forward(request, response);
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
