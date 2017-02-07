package souvenirs.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import tool.Base64;
import user.web.UserManager;

/**
 * Servlet implementation class SouvenirsAjax
 * 处理Souvenirs相关的Ajax请求的Servlet
 */
//@WebServlet({ "/changeAlbumName", "/changeDescription", "/sendComment" })
public class SouvenirsAjaxServ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(SouvenirsAjaxServ.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SouvenirsAjaxServ() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Control层，获取前台传来的参数并分发给对应的Manager层方法；收取Manager层处理的结果并返回给前台
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
			SouvenirsAjaxManager sm = SouvenirsAjaxManager.getInstance();
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

			logger.debug(request.getQueryString());
			String result = new String();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);
			logger.debug("query_url "+query_url);
			PrintWriter out = response.getWriter();
			try {
				if (query_url.contentEquals("updateAlbumName"))
					result = sm.updateAlbumName(para);
				else if (query_url.contentEquals("updateDescription")) {
					result = sm.updateDescription(para);
				} else if (query_url.contentEquals("deletePicture")){
					result = sm.deletePicture(para);
				} else if (query_url.contentEquals("updateAlbumCover")){
					result = sm.updateAlbumCover(para);
				} else if (query_url.contentEquals("queryAlbumCover")){
					result = sm.queryAlbumCover(para);
				} else if (query_url.contentEquals("AlbumAjax")) {
					result = sm.queryPictureInAlbum(para);
				} else if (query_url.contentEquals("sharePictures")) {
					result = sm.sharePictures(para);
				}else {
					;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.getStackTrace();
				logger.warn("", e);
				result = e.getMessage();
			}
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
