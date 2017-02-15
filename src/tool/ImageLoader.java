package tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
 * ImageLoader��������ͼƬurl�ı��롢��������ʾ��
	 * <p>
	 * ��Ŀ�е�ͼƬ��4�ֿ�����Դ��<br>
	 * 1. ����ĳ���û������, 3������(user_id, album_name, filename)<br>
	 * 2. ����ĳ�û���ͷ��1������(user_id) <br>
	 * 3. ����ĳ���ķ��棬2������(user_id, album_name) <br>
	 * 4. ����ĳС��Ĺ������ķ��棬1������(group_id) <br>
	 * </p>
	 * <pre>
	 * �����������������εĲ�����һ����Ҫ4��������������������£�<br>
	 *     method    para1        para2           para3             privilege<br>
	 * 1   direct      user_id  album_name  filename    user self+users in shared group<br>
	 * 2   query      "user"       user_id            ����             user self <br>
	 * 3   query      "album"   user_id      album_name       user self<br>    
	 * 4   query      "group"  group_id          ����         users in this group<br>
	 * </pre>
	 * ͼƬurl�ı��������������ļ����ĵ���
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
	 * ����ͼƬ��url��ȡͼƬ�����Զ����Ƶ���ʽ���������������������������á�
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		String user_id = "";
		//Check validation of session
		if (session.isNew()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			//logger.debug("Invalid Login Session");
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
				//logger.debug("file_path: "+file_path);
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
				
				// If download operation is specified, form picture's filename
				if (request.getParameter("download")!=null && request.getParameter("download").contentEquals("true"))
					response.setHeader("Content-Disposition", "attachment;filename="+java.net.URLEncoder.encode(file_path.substring(file_path.lastIndexOf(File.separator)+1), "UTF-8"));
				
				os = response.getOutputStream();
				int n;
				while ((n = bis.read(buffer)) != -1) {
					os.write(buffer, 0, n);
				}
				bis.close();
				os.flush();
				os.close();
			} catch (Exception e) {
				//There are something wrong with reading and sending image
				logger.warn(e.getMessage() + ", request: <" + request.getQueryString() + ">, request user id = <"
						+ user_id + ">");
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
	}

	/**
	 * ����doGet����
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * ��̬����genImageQuery���������û���ָ��ͨ��base64����ͼƬ��url��������Ϊ�ײ�������������ṩ���û��Ľӿڡ�
	 * @param isDirect ��ʾҪ���ʵ�ͼƬ���û�����е�ͼƬ���Ǹ�����������Ŀ�ϵ�ͼƬ(�������ͼ��ͷ��ͼ)
	 * @param para ���3��Ԫ�أ�����һ���Ĺ���洢�˻�ȡ���ͼƬ��Ҫ�Ĳ�����<strong>������url��ʱ�򲢲���Ҫ����������������ĺ���</strong>
	 * @return ��ͼƬ�ķ���url�ַ���
	 */
	private static String genImageQuery(boolean isDirect, List<String> para) {
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
	
	/**
	 * genPathFromPara����ͼƬ��url���ɷ��ʸ�ͼƬ���base_path��·����ͬʱ����Ȩ�޼��<br>

	 * @param method ���ֿ��ܣ�"direct"��"query", ��ʾҪ���ʵ�ͼƬ���û�����е�ͼƬ���Ǹ�����������Ŀ�ϵ�ͼƬ(�������ͼ��ͷ��ͼ)
	 * @param content_base64 �洢�˽����Ĳ���
	 * @param user_id ���в������û�ID
	 * @return ��ͼƬ�����base_path��·��
	 * @throws Exception ��ͼƬurl�޷������������û�û��Ȩ�������ͼƬ��ʱ����׳��쳣
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
		List<String> para = null;
		//logger.debug("getPathFromPara paras:"+method+", "+content_base64+", "+user_id+", "+para_str);
		if (method.contentEquals("direct")) {
			para = Arrays.asList(user_id, para_str[0], para_str[1], para_str[2]);
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
				if (true || para_str[1].contentEquals(user_id))//Remove privillege checking of querying user's avatar
					// Query avatar path of user stored in database
					try {
						logger.debug(para);
						path = (String) DB.execSQLQuery("select avatar from user where user_id= ?", Arrays.asList(para_str[1]))
								.get(0).get(0);
					} catch (IndexOutOfBoundsException e) {
						// Cannot find result of username
						return "";
					}
				else {
					throw new Exception("Request is refused because of invalid privillege.");
				}
			} else if (para_str[0].contentEquals("group")) {
				para = Arrays.asList(user_id, para_str[1]);
				if ((long) DB.execSQLQuery(
						"select count(*) from user_belong_group where user_id=? and group_id=?", para)
						.get(0).get(0) == 1)
					try {
						path = (String) DB.execSQLQuery("select album_cover from souvenirs.group where group_id=?", 
								Arrays.asList(para_str[1])).get(0)	.get(0);
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
				para = Arrays.asList(para_str[1], para_str[2]);
				if (para_str[1].contentEquals(user_id))
					try {
						path = (String) DB.execSQLQuery(
								"select album_cover from album where user_id=? and album_name = ?", para)	.get(0).get(0);
						//logger.debug("album_cover_path: "+path);
					} catch (Exception e) {
						// TODO: handle exception
						//logger.debug("", e);
						throw e;
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
	
	/**
	 * ͨ����Ƭ�������û�ID������������ƺ��ļ������ɸ���Ƭ�ĵ�ַ
	 * @param user_id ��Ƭ�������û�ID
	 * @param album_name ��Ƭ���������
	 * @param filename ��Ƭ�ļ���
	 * @return ���ɵ�URL�ַ���
	 */
	public static String genAddrOfPicture(String user_id, String album_name, String filename) {
		List<String> para = Arrays.asList(user_id, album_name, filename);
		return genImageQuery(true, para);
	}
	
	/**
	 * ͨ���û�ID�����û�ͷ��ĵ�ַ
	 * @param user_id �û�ID
	 * @return �û�ͷ��ĵ�ַ
	 */
	public static String genAddrOfAvatar(String user_id) {
		List<String> para = Arrays.asList("user", user_id);
		return genImageQuery(false, para);
	}
	
	/**
	 * ͨ���û�ID������������ɸ���������ĵ�ַ
	 * @param user_id �û�ID
	 * @param album_name �������
	 * @return ����������ĵ�ַ
	 */
	public static String genAddrOfPAlbumCover(String user_id, String album_name) {
		List<String> para = Arrays.asList("album", user_id, album_name);
		return genImageQuery(false, para);
	}
	
	/**
	 * ͨ��С��ID����С���Ӧ�Ĺ������ķ����ַ
	 * @param group_id С��ID
	 * @return ���ڸ�С��Ĺ������ķ����ַ
	 */
	public static String genAddrOfSAlbumCover(String group_id) {
		List<String> para = Arrays.asList("group", group_id);
		return genImageQuery(false, para);
	}
	
}
