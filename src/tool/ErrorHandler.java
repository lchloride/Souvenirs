package tool;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ErrorHandler
 */
//@WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		Throwable throwable = (Throwable)	request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String)request.getAttribute("javax.servlet.error.servlet_name");
		if (servletName == null){
			servletName = "Unknown";
		}
		String requestUri = (String)request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null){
			requestUri = "Unknown";
		}
		String description = "";
		String des_img_path = "";
		if (throwable == null && statusCode == null){
	      	description = "错误信息丢失";
	   	}else if (statusCode != null) {
	      	switch (statusCode) {
			case 404:
				des_img_path = "/Souvenirs/res/image/404_not_found.png";
				description = "Sorry, the page you requested has been moved or deleted. ";
				break;

			default:
				break;
			}
		}else{
		   	description = "错误信息--Servlet Name : " + servletName + "<br/>";
	      	description += "异常类型 : " +throwable.getClass( ).getName( ) +"</br>";
	      	description += "请求 URI: " + requestUri +"<br>";
	      	description += "异常信息: " + throwable.getMessage( );
	   	}
		request.setAttribute("Description", description);
		request.setAttribute("DesImage", des_img_path);
		request.setAttribute("ErrorCode", statusCode);
		request.setAttribute("LastPage", request.getHeader("Referer"));
		RequestDispatcher dispatcher = request.getRequestDispatcher("ErrorPage.jsp");
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
