package souvenirs.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import tool.ImageLoader;

public class SouvenirsManager {
	private static SouvenirsManager souvenirs_manager = new SouvenirsManager();
	private Map<String, String> parameter = null;
	private static Logger logger = Logger.getLogger(SouvenirsManager.class);
	
	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}
	
	public static SouvenirsManager getInstance() {
		return souvenirs_manager;
	}
	
	public Map<String, Object> displayContent() {
		Map<String, Object> result = new HashMap<>();
		List<String> para = new ArrayList<>();
		para.add("user");
		para.add(parameter.get("login_user_id"));
		logger.debug("Image Query Parameter: "+para);
		result.put("Avatar" ,ImageLoader.genImageQuery(false, para));
		result.put("DispatchURL", "homepage.jsp");
		return result;
	}

}
