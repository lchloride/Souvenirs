package tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			
			String base_path = PropertyOper.GetValueByKey("souvenirs.properties", "data_path");
			String file_path = getPathFromPara(request.getParameter("method"), request.getParameter("content"));
			System.out.println("ImageLoader file_path:"+file_path);
			if (file_path.isEmpty()) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			String img = base_path+file_path;
			
			BufferedInputStream bis = null;
			OutputStream os = null;
			FileInputStream fileInputStream = new FileInputStream(new File(img));

			bis = new BufferedInputStream(fileInputStream);
			byte[] buffer = new byte[512];
			response.reset();
			response.setCharacterEncoding("UTF-8");
			// 不同类型的文件对应不同的MIME类型
			response.setContentType("image/*");
			// 文件以流的方式发送到客户端浏览器
			// response.setHeader("Content-Disposition","attachment;
			// filename=img.jpg");
			// response.setHeader("Content-Disposition", "inline;
			// filename=img.jpg");

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
			e.printStackTrace();
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
		
		query += "&content="+Base64.encode(content);
		return query;
	}
	
	private String getPathFromPara(String method, String content_base64) {
		if (method == null || method.isEmpty()) {
			logger.warn("ImageLoader METHOD parameter missed");
			return "";
		}
		if (content_base64 == null || content_base64.isEmpty()) {
			logger.warn("ImageLoader CONTENT parameter missed");
			return "";
		}
		String path = "";
		String content = Base64.decode(content_base64);
		String[] para = content.split("/");
		if (method.contentEquals("direct")) {
			path = File.separator+para[0]+File.separator+para[1]+File.separator+para[2];
		}else if (method.contentEquals("query")) {
			if (para[0].contentEquals("user")) {
				//logger.debug("ImageLoader Para:"+para[0] + " " + para[1]);
				try {
					path = (String)DB.execSQLQuery("select avatar from user where username='"+para[1]+"'").get(0).get(0);
				} catch (IndexOutOfBoundsException e) {
					//Cannot find result of username
					return "";
				}
			}else if (para[0].contentEquals("group")) {
				try {
					path = (String)DB.execSQLQuery("select album_cover from group where user_id='"+para[1]+"'").get(0).get(0);
				} catch (IndexOutOfBoundsException e) {
					// TODO: handle exception
					return "";
				}
			} else if (para[0].contentEquals("album")) {
				try {
					path = (String)DB.execSQLQuery("select album_cover from group where user_id='"+para[1]+"' and album_name = '"+para[2]+"'").get(0).get(0);
				} catch (Exception e) {
					// TODO: handle exception
					return "";
				}
			} else 
				;
		}else {
			;
		}
		return path;
	}
}
