package souvenirs.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import tool.Base64;
import tool.PropertyOper;
import tool.exception.BadRequestException;
import user.web.UserManager;

/**
 * Souvenirs Servlet���Ʋ㣺�����ǰ̨��ȡ���ݣ�����û�session��¼������url������Ӧ��ҵ�񷽷�����ȡҵ�񷽷����ص����ݲ����͸���Ӧ��ǰ��ҳ��
 */
/* @WebServlet("/SouvenirsServ") */
public class SouvenirsServ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * �ϴ��ڴ���ֵ(�ļ��������Ĵ�С)
	 */
	private static final int MEMORY_THRESHOLD = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MEMORY_THRESHOLD"), 16);
	/**
	 * �ϴ��ļ�������С
	 */
	private static final int MAX_FILE_SIZE = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_FILE_SIZE"), 16);
	/**
	 * �ɽ��յĶ�������������С
	 */
	private static final int MAX_REQUEST_SIZE = Integer
			.parseInt(PropertyOper.GetValueByKey("souvenirs.properties", "MAX_REQUEST_SIZE"), 16); // *

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
	 * ���Ʋ��ȡ-�ַ��ķ���
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
				if (request.getMethod().contentEquals("GET"))
					para.put(paraName, URLDecoder.decode(paraValue, "UTF-8"));
				else
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
				if (query_url.contentEquals("homepageold")) {
					
					// Display Content when firstly open the page
					result = sm.displayContentOld(para);
				} else if (query_url.contentEquals("homepage")) {
					
					// Display Content when firstly open the page
					result = sm.displayContent(para);
				} else if (query_url.contentEquals("formPicture")) {
					
					// Covert base64 code of image to bit flow and send it back to front side as an attachment
					// This method is called when downloading souvenir
					formPicture(response, session, para.get("picture"), para.get("Text_souvenir_name"));
					return;
				} else if (query_url.contentEquals("making")) {
					
					// Display making page when firstly open making page
					result = sm.displayMakingSouvenirs(para);
				} else if (query_url.contains("AlbumAjax")) {
					
					// Send json string of album info when front side open an Ajax query
					//queryAlbumAjax(response, sm, para);
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
					
					//Display personal album management content
					result = sm.displayPAlbumManager(para);
				} else if (query_url.contentEquals("sharedAlbum")) {
					
					//Display shared album management content
					result = sm.displaySAlbumManager(para);
				} else if (query_url.contentEquals("picture") && request.getParameter("user_id")==null) {
					
					//Display picture management content in personal album
					result = sm.displayPictureManager(para);
				} else if (query_url.contentEquals("picture") && request.getParameter("user_id")!=null) {
					
					//Display picture management content in shared album
					result = sm.displaySPictureManager(para);
				} else if (query_url.contentEquals("showPicture")) {
					
					//Show picture in original size 
					result = sm.showPicture(para);
				} else if (query_url.contentEquals("share")) {
					
					//Show picture in original size 
					result = sm.displaySharePicture(para);
				} else if (query_url.contentEquals("updatePictureInfo")) {
					
					//Show picture in original size 
					result = sm.updatePictureInfo(para);
				} else {
					
					// query_url is wrong
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} catch(BadRequestException bre) {
				logger.error("Souvenirs servlet throws an exception", bre);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}catch (Exception e) {
				logger.error("Souvenirs servlet throws an exception", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			for (Entry<String, Object> entry : result.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			
			// Forward or redirect to assigned page
			String dispatchURL = result.containsKey("DispatchURL") ? (String) result.get("DispatchURL") : "index.jsp";
			if (result.containsKey("Is_redirect"))
				response.sendRedirect(dispatchURL);
			else {
				RequestDispatcher dispatcher = request.getRequestDispatcher(dispatchURL);
				dispatcher.forward(request, response);
			}
		}
	}

	/**
	 * ����doGet����
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String query_url = request.getServletPath();
		query_url = query_url.substring(query_url.lastIndexOf('/') + 1);
		
		if (query_url.contentEquals("createAlbum")) {
			
			HttpSession session = request.getSession(true);
			// If login information is wrong, redirect to index.jsp in order to
			// login in again
			logger.debug(session + " " + session.getAttribute("username") + " " + session.getAttribute("password"));
			if (!UserManager.checkLogin(session.getAttribute("user_id"), session.getAttribute("username"),
					session.getAttribute("password"))) {
				session.invalidate();
				response.sendRedirect("loginfail.jsp");
			} else {
				// ����Ƿ�Ϊ��ý���ϴ�
				if (!ServletFileUpload.isMultipartContent(request)) {
					// ���������ֹͣ
					PrintWriter writer = response.getWriter();
					writer.println("Error: Form type must be 'enctype=multipart/form-data'");
					writer.flush();
					return;
				}

				// �����ϴ�����
				DiskFileItemFactory factory = new DiskFileItemFactory();
				// �����ڴ��ٽ�ֵ - �����󽫲�����ʱ�ļ����洢����ʱĿ¼��
				factory.setSizeThreshold(MEMORY_THRESHOLD);
				// ������ʱ�洢Ŀ¼
				factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

				ServletFileUpload upload = new ServletFileUpload(factory);

				// ��������ļ��ϴ�ֵ
				upload.setFileSizeMax(MAX_FILE_SIZE);

				// �����������ֵ (�����ļ��ͱ�����)
				upload.setSizeMax(MAX_REQUEST_SIZE);

				Map<String, String> para = new HashMap<>();
				String result = "index.jsp";
				SouvenirsManager sm = SouvenirsManager.getInstance();

				FileItem file_item = null;
				try {
					// ���������������ȡ�ļ�����
					List<FileItem> formItems = upload.parseRequest(request);
					if (formItems != null && formItems.size() > 0) {
						// ����������
						for (FileItem item : formItems) {
							if (item.isFormField()) {
								String key = item.getFieldName();
								String val = new String(item.getString().getBytes("ISO-8859-1"), "UTF-8");
								para.put(key, val);
							} else {
								// Store file handler
								file_item = item;
								// �ڱ���������Java���õ�Ĭ���ַ�������GBK������ǰ�˷����Ķ�������δָ���������룬Java��ʹ��Ĭ�ϵ�GBK������
								// ���GBK���ļ����浽�ļ��У����ıض������룬����Ҫ���ļ���ת��UTF-8��
								// ���ڲ�ͬJava���ò�ͬ�����Զ��ļ���ͳһ����һ��ת�룬��֤������
								para.put("origin_filename", new String(
										item.getName().getBytes(System.getProperty("sun.jnu.encoding")), "UTF-8"));
								logger.debug(item.getName());
							}
						}
					}
					// Send user_id as primary key of user to manager object
					para.put("login_user_id",
							session.getAttribute("user_id") == null ? "" : (String) session.getAttribute("user_id"));

					// Send parameters and file handler to UploadManager
					result = sm.createPAlbum(para, file_item);

				} catch (Exception ex) {
					ex.printStackTrace();
					request.setAttribute("Upload_result", ex.getMessage());
				}

				// ��ת�� upload.jsp
				response.sendRedirect(result);
			}
		} else {
			doGet(request, response);
		}
		
	}

	/**
	 * ����ͼƬBase64�ַ���(����MIME��Ϣ)����ͼƬ�Ķ������ļ�������ǰ�ˡ�������ת��pngͼƬ��ʽ���������������õ�Souvenir��
	 * <p>
	 * ���͸�ǰ�˵����ݸ�ʽ��ͼƬ��������������������д�����Ӧ��Ӧ�ķ�����
	 * </p>
	 * @param response Servlet��response����
	 * @param session Servlet��session����
	 * @param content ͼƬBase64�ַ���������MIMEԪ����
	 * @param filename ���ص�souvenirͼƬ���ļ���
	 * @throws Exception ��ǰ�˷�������ʧ��ʱ���׳��쳣
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
	 * ��ȡĳ���û���һ������е�ȫ����Ƭ��Ϣ������json�ַ�������ʽ����ǰ�ˡ���������Ӧ��ȡalbum��ͼƬ��ajax����.
	 * <p>
	 * ���͵�ǰ�˵����ݸ�ʽ��<br>
	 * json�ַ������ɶ��������ɵ�json���飻����ÿ��json��������������ɣ�Filename��Addr
	 * </p>
	 * @param response Servlet��response
	 * @param sm SouvenirsManager��������
	 * @param para ����SouvenirsManager�Ĳ�����key����login_user_id(��¼�û���)��album_identifier(����ʶ����
	 * 				personal albumָ����album name��shared albumָ����group_id)
	 * @throws Exception �������ʧ�����׳��쳣
	 * @see souvenirs.web.SouvenirsManager#getImageAddrInAlbum(Map)
	 * @see souvenirs.web.SouvenirsAjaxManager#queryPictureInAlbum(Map)
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void queryAlbumAjax(HttpServletResponse response, SouvenirsManager sm, Map<String, String> para) throws Exception {
		logger.info("User(id=<" + para.get("login_user_id") + ">) query image address in album <"
				+ para.get("album_identifier") + ">.");
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
					+ ">) obtained json string of image address in album <" + para.get("album_identifier") + ">");
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}		
	}
	
	/**
	 * ���souvenir�������Ƿ���ɡ��÷�����ǰ�˶�ʱ��ȡ��̨�������ȵ�ʱ����á�<br>
	 * <p>
	 * ������ɵı�־����session�У�keyΪsouvenir_making_done���ñ�־������״̬�������ڣ�false��true��
	 * ���в����ڱ�ʾδ����souvenir��������false��ʾ�����������ǻ�δ��ɣ�true��ʾ�Ѿ�������ɡ�
	 * ת����ϵ�ǣ�������--&gt;false--&gt;true--&gt;������....<br>
	 * ���ñ�־λtrue��ʱ�����session����souvenir_making_err_msg������ԣ�˵������ʧ�ܣ�����˵�������ɹ���<br>
	 * </p>
	 * <p>
	 * ���͵�ǰ�˵���Ϣ��ʽ��<br>
	 * ������޴���----"true"<br>
	 * ��ɵ��д���----"true, "+������Ϣ<br>
	 * δ���----"false" <br>
	 * </p>
	 * @param response Servlet��response
	 * @param session servlet��session
	 * @throws Exception ��ǰ̨����ʧ��ʱ�׳��쳣
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
			logger.warn("����һ���ַ��������쳣������û����." + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
}
