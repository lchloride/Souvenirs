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
 * Souvenirs Servlet控制层：负责从前台收取数据，检查用户session登录，根据url调用相应的业务方法，收取业务方法返回的数据并发送给对应的前端页面
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

	/*
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * 控制层获取-分发的方法
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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

			Map<String, Object> result = new HashMap<>();

			// Obtain operation
			String query_url = request.getServletPath();
			query_url = query_url.substring(query_url.lastIndexOf('/') + 1);

			logger.debug("query_url:<"+query_url+">");
			try {
				// Select and call specific function according query_url
				if (query_url.contentEquals("homepage")) {
					
					// Display Content when firstly open the page
					result = sm.displayContent(para);
				} else if (query_url.contentEquals("formPicture")) {
					
					// Covert base64 code of image to bit flow and send it back to front side as an attachment
					// This method is called when downloading souvenir
					formPicture(response, session, para.get("picture"), para.get("Text_souvenir_name"));
					return;
				} else if (query_url.contentEquals("making")) {
					
					// Display making page when firstly open making page
					result = sm.makingSouvenirs(para);
				} else if (query_url.contains("AlbumAjax")) {
					
					// Send json string of album info when front side open an Ajax query
					queryAlbumAjax(response, sm, para);
					return;
				} else if (query_url.contentEquals("checkMakingDone")) {
					
					// This part of code aims to check whether souvenir making has been done or not,which helps browser
					// load proper page(modal).
					// Browser asks for making flag. If making has been done, it will send "true" to browser, else it
					// will send "false" to browser.
					// To obtain making flag in time, browser should asks for it in a specific period.
					checkMakingDone(response, session);
					return;
				} else if (query_url.contentEquals("album")) {
					result = sm.displayAlbumManager(para);
				}else {
					
					// query_url is wrong
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} catch (Exception e) {
				logger.error("Souvenirs servlet throws an exception", e);
				if (e.getMessage().contentEquals("Invalid Parameter user_id OR album_name"))
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				else
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
	 * 调用doGet方法
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * 根据图片Base64字符串(包含MIME信息)生成图片的二进制文件并发给前端。本方法转成png图片格式，用于下载制作好的Souvenir。
	 * <p>
	 * 发送给前端的数据格式：图片二进制流，浏览器会自行处理并响应响应的方法。
	 * </p>
	 * @param response Servlet的response对象
	 * @param session Servlet的session对象
	 * @param content 图片Base64字符串，包含MIME元数据
	 * @param filename 下载的souvenir图片的文件名
	 * @throws Exception 向前端发送数据失败时会抛出异常
	 */
	private void formPicture(HttpServletResponse response, HttpSession session, String content, String filename) throws Exception {
		session.setAttribute("souvenir_making_done", false);
		if (filename == null ||filename.isEmpty())
			filename = "img";
		String content_base64 = content.substring(content.indexOf("base64,") + 7, content.length());
		byte[] rs_byte = Base64.decodeBytes(content_base64);
		logger.debug("Base64 code length:" + content_base64.length() + ", Image bit length:" + rs_byte.length);
		logger.info("User(id=<" + session.getAttribute("user_id") + ">) made a souvenir, original base64 code size:<"
				+ content_base64.length() + ">");
		try {
			OutputStream os = null;
			Thread.sleep(5000);
			response.reset();
			response.setCharacterEncoding("UTF-8");
			// Set MIME type as image
			response.setContentType("image/*");
			// Set content as attachment and bit flow
			response.setHeader("Content-Disposition", "attachment;filename="+java.net.URLEncoder.encode(filename+".png", "UTF-8"));
			// response.setHeader("Content-Disposition","inline;filename=img.jpg");
			// Set content length
			response.setContentLength(rs_byte.length);

			os = response.getOutputStream();
			os.write(rs_byte);
			os.flush();
			os.close();
			logger.info("User(id=<" + session.getAttribute("user_id") + ">) making souvenir succeeded.");
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("User(id=<" + session.getAttribute("user_id") + ">) making souvenir failed. ErrorMsg:<"
					+ e.getMessage() + ">");
			session.setAttribute("souvenir_making_err_msg", e.getMessage());
			throw e;
		}
		// Set the flag of making done to session, in order to support checking from browser
		// Reference of URL checkMakingDone processing part
		session.setAttribute("souvenir_making_done", true);
		return;
	}
	
	/**
	 * 获取某个用户的一个相册中的全部照片信息，并以json字符串的形式发给前端。本方法响应获取album中图片的ajax请求.
	 * <p>
	 * 发送到前端的数据格式：<br>
	 * json字符串，由多个对象组成的json数组；其中每个json对象由两个域组成：Filename与Addr
	 * </p>
	 * @param response Servlet的response
	 * @param sm SouvenirsManager操作对象
	 * @param para 发给SouvenirsManager的参数表
	 * @throws Exception 如果发送失败则抛出异常
	 * @see souvenirs.web.SouvenirsManager#getImageAddrInAlbum(Map)
	 */
	private void queryAlbumAjax(HttpServletResponse response, SouvenirsManager sm, Map<String, String> para) throws Exception {
		logger.info("User(id=<" + para.get("login_user_id") + ">) query image address in album <"
				+ para.get("album_name") + ">.");
		String rs = sm.getImageAddrInAlbum(para);
		response.setContentType("text/xml; charset=UTF-8");
		// Let browser not to store cache
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		try {
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			logger.info("User(id=<" + para.get("login_user_id")
					+ ">) obtained json string of image address in album <" + para.get("album_name") + ">");
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}		
	}
	
	/**
	 * 检查souvenir的制作是否完成。该方法在前端定时获取后台制作进度的时候调用。<br>
	 * <p>
	 * 制作完成的标志存在session中，key为souvenir_making_done。该标志有三种状态：不存在，false，true。
	 * 其中不存在表示未进行souvenir的制作；false表示正在制作但是还未完成；true表示已经制作完成。
	 * 转化关系是：不存在--&gt;false--&gt;true--&gt;不存在....<br>
	 * 当该标志位true的时候，如果session中有souvenir_making_err_msg这个属性，说明制作失败；否则说明制作成功。<br>
	 * </p>
	 * <p>
	 * 发送到前端的消息格式：<br>
	 * 完成且无错误----"true"<br>
	 * 完成但有错误----"true, "+错误信息<br>
	 * 未完成----"false" <br>
	 * </p>
	 * @param response Servlet的response
	 * @param session servlet的session
	 * @throws Exception 向前台发送失败时抛出异常
	 */
	private void checkMakingDone(HttpServletResponse response, HttpSession session) throws Exception {
		String rs = new String("false");
		if (session.getAttribute("souvenir_making_done") == null
				|| !(boolean) session.getAttribute("souvenir_making_done"))
			// invalid making flag or making flag equals to false
			rs = "false";
		else {
			// making flag equals to true, which means souvenir making has been done
			rs = "true";
			// If attribute souvenir_making_err_msg in session is valid, there are some errors when making souvenir.
			// In this situation, send error message to browser.
			if (session.getAttribute("souvenir_making_err_msg") != null) {
				rs += ", " + session.getAttribute("souvenir_making_err_msg");
				session.removeAttribute("souvenir_making_err_msg");
				logger.info("Notify user's(id=<" + session.getAttribute("user_id")
						+ ">) browser that making souvenir failed.");
			} else
				logger.info("Notify user's(id=<" + session.getAttribute("user_id")
						+ ">) browser that making souvenir succeed.");
			session.removeAttribute("souvenir_making_done");
		}
		response.setContentType("text/xml; charset=UTF-8");
		// Let browser not to store cache
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		try {
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn("发送一个字符串还有异常，真是没救了." + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
}
