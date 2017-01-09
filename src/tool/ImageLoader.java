package tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class ImageLoader
 */
// @WebServlet("/ImageLoader")
public class ImageLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ImageLoader.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageLoader() {
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
	 *  根据图片的url获取图片，并以二进制的形式发给浏览器    
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		String user_id = "";
		//Check validation of session
		if (session.isNew()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			logger.debug("Invalid Login Session");
			return;
		} else {
			try {
				user_id = session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id");
				//Set header of HTTP
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				
				//base_path is the folder storing all users' pictures
				String base_path = PropertyOper.GetValueByKey("souvenirs.properties", "data_path");
				//file_path is the true path that is formed from url based on parameters method and content 
				String file_path = getPathFromPara(request.getParameter("method"), request.getParameter("content"),
						user_id);
				
				if (file_path.isEmpty()) {
					throw new Exception("Invalid file path");
				}
				String img = base_path + file_path;

				BufferedInputStream bis = null;
				OutputStream os = null;
				FileInputStream fileInputStream = new FileInputStream(new File(img));
				
				//Send bytes of image to browser
				bis = new BufferedInputStream(fileInputStream);
				byte[] buffer = new byte[512];
				response.reset();
				response.setCharacterEncoding("UTF-8");
				// Set MIME of image
				response.setContentType("image/*");
				response.setContentLength(bis.available());

				os = response.getOutputStream();
				int n;
				while ((n = bis.read(buffer)) != -1) {
					os.write(buffer, 0, n);
				}
				bis.close();
				os.flush();
				os.close();
			} catch (Exception e) {
				// e.printStackTrace();
				//There are something wrong with reading and sending image
				logger.info(e.getMessage() + ", request: <" + request.getQueryString() + ">, request user id = <"
						+ user_id + ">");
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
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

	/*
	 * 静态函数genImageQuery用来根据用户的指令通过base64生成图片的url<br>
	 * @param isDirect 标示要访问的图片是用户相册中的图片还是附属于其他项目上的图片(比如封面图、头像图)
	 * @param para 最多3个元素，按照一定的规则存储了获取这个图片需要的参数，在生成url的时候并不需要关心三个参数代表的含义
	 * @result 该图片的访问url
	 */
	public static String genImageQuery(boolean isDirect, List<String> para) {
		String query = "/Souvenirs/image?method=";
		String content = null;
		if (isDirect)
			query += "direct";
		else
			query += "query";

		if (para.size() < 2 || para.size() > 3)
			return null;
		else if (para.size() == 2)
			content = para.get(0) + "/" + para.get(1);
		else
			content = para.get(0) + "/" + para.get(1) + "/" + para.get(2);

		query += "&content=" + Base64.encode(content);
		Logger logger = Logger.getLogger(ImageLoader.class);
		logger.debug("Image Query URL: " + query);
		return query;
	}
	
	/*
	 * genPathFromPara根据图片的url生成访问该图片相对base_path的路径，同时进行权限检查<br/>
	 * 项目中的图片有4种可能来源：<br>
	 * 1. 来自某个用户的相册, 3个参数(user_id, album_name, filename)<br>
	 * 2. 来自某用户的头像，1个参数(user_id) <br>
	 * 3. 来自某相册的封面，2个参数(user_id, album_name) <br>
	 * 4. 来自某小组的共享相册的封面，1个参数(group_id) <br>
	 * 加上区分这四种情形的参数，一共需要4个参数。参数表分配如下：<br>
	 *     method    para1        para2           para3          privilege<br>
	 * 1   direct      user_id  album_name  filename    user self&users in shared group<br>
	 * 2   query      "user"       user_id         ---------          user self <br>
	 * 3   query      "album"   user_id       album_name    user self<br>    
	 * 4   query      "group"  group_id       ---------       users in this group<br>
	 * @param method 两种可能："direct"或"query，"标示要访问的图片是用户相册中的图片还是附属于其他项目上的图片(比如封面图、头像图)
	 * @param content_base64 存储了解析的参数
	 * @param user_id 进行操作的用户ID
	 * @result 该图片相对于base_path的路径
	 * 
	 */
	private String getPathFromPara(String method, String content_base64, String user_id) throws Exception {
		if (method == null || method.isEmpty()) {
			logger.warn("ImageLoader METHOD parameter missed");
			return "";
		}
		if (content_base64 == null || content_base64.isEmpty()) {
			logger.warn("ImageLoader CONTENT parameter missed");
			return "";
		}
		if (user_id.isEmpty()) {
			return "";
		}
		String path = "";
		String content = Base64.decode(content_base64);
		String[] para_str = content.split("/");
		List<String> para = Arrays.asList(para_str);
		if (method.contentEquals("direct")) {
			para = new ArrayList<>(4);
			para.add(user_id);
			para.add(para_str[0]);
			para.add(para_str[1]);
			para.add(para_str[2]);
			// Two situations: one is request user's own image, just check
			if (para_str[0].contentEquals(user_id) || (long) DB.execSQLQuery(
					"select count(*) from check_image_priv_direct where allowed_user_id = ? and owner_id= ? and album_name = ? and filename = ?",
					para).get(0).get(0) == 1)
				path = File.separator + para_str[0] + File.separator + para_str[1] + File.separator + para_str[2];
			else {
				logger.debug("query: select count(*) from check_image_priv_direct where allowed_user_id = '" + user_id
						+ "' and owner_id = '" + para_str[0] + "' and album_name = '" + para_str[1]
						+ "' and filename = '" + para_str[2] + "'");
				throw new Exception("Request is refused because of invalid privillege.");
			}
		} else if (method.contentEquals("query")) {
			if (para_str[0].contentEquals("user")) {
				if (para_str[1].contentEquals(user_id))
					// Query avatar path of user stored in database
					try {
						logger.debug(para);
						path = (String) DB
								.execSQLQuery("select avatar from user where user_id= ?", Arrays.asList(para_str[1]))
								.get(0).get(0);
					} catch (IndexOutOfBoundsException e) {
						// Cannot find result of username
						return "";
					}
				else {
					throw new Exception("Request is refused because of invalid privillege.");
				}
			} else if (para_str[0].contentEquals("group")) {
				if ((long) DB
						.execSQLQuery("select count(*) from user_belong_group where user_id=? and group_id=?", para)
						.get(0).get(0) == 1)
					try {
						path = (String) DB
								.execSQLQuery("select album_cover from souvenirs.group where group_id=?", Arrays.asList(para_str[1])).get(0)
								.get(0);
					} catch (IndexOutOfBoundsException e) {
						// TODO: handle exception
						return "";
					}
				else {
					logger.debug("query: select count(*) from user_belong_group where user_id='" + user_id
							+ "' and group_id='" + para_str[1] + "'");
					throw new Exception("Request is refused because of invalid privillege.");
				}
			} else if (para_str[0].contentEquals("album")) {
				if (para_str[1].contentEquals(user_id))
					try {
						path = (String) DB
								.execSQLQuery("select album_cover from album where user_id=? and album_name = ?", para)
								.get(0).get(0);
					} catch (Exception e) {
						// TODO: handle exception
						return "";
					}
				else
					throw new Exception("Request is refused because of invalid privillege.");
			} else
				throw new Exception("Invalid parameters of image URL");
		} else {
			throw new Exception("Invalid parameters of image URL");
		}
		return path;
	}
}
