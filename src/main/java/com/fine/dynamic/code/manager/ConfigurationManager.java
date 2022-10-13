package com.fine.dynamic.code.manager;

import com.alibaba.fastjson.JSONObject;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service("ConfigurationManager")
public class ConfigurationManager {
	static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	public static String CONF = "conf";
	private static final Map<String, ConfigObject> MAP_CONFIG = new HashMap<String, ConfigObject>();


//	private static ConfigrationManager instance = new ConfigrationManager();
	private ConfigurationManager() {
		logger.info("-------------------------ConfigrationManager.loadDefault 中文显示测试---------------------------");
		loadDefault();
		logger.info("--------------------------------------------------------------------------------------------");
	}
//	public static getInstance() {
//		return instance;
//	}

	public Object load(String path) {
		if(path==null||path.isEmpty()) {
			loadDefault();
			return getAll();
		}
		File file = new File(CONF+"\\"+path+".groovy");
		return load(file);
	}

	public Object getAll() {
		return JSONObject.toJSON(MAP_CONFIG);
	}

	private String loadDefault() {
		String pathDefault = CONF;
		File file = new File(pathDefault);
		if(!file.exists()) {
			file.mkdirs();
		}
		File[] list = file.listFiles();
		if (list == null || list.length < 1) {
			return null;
		}
		for (File f : list) {
			logger.info("load file:" + f);
			Object content = load(f);
			if(content!=null) {
				logger.info(content.toString());
			} else {
				logger.info("ERROR:null");
			}
		}
		return Arrays.toString(list);
	}

	public Object load(File file) {
		Path pa = file.toPath();
		String key = getConfigKey(pa);
		ConfigObject config = null;
		try {
			config = new ConfigSlurper().parse(file.toURI().toURL());
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR:parse exception; path:"+pa;
		}
//		if(config==null) {
//			return null;
//		}
		if(config != null && MAP_CONFIG.containsKey(key)) {
			MAP_CONFIG.get(key).merge(config);
		} else {
			MAP_CONFIG.put(key, config);
		}
		return JSONObject.toJSON(MAP_CONFIG.get(key));
	}

	public static ConfigObject getConfig(String configKey) {
		return MAP_CONFIG.get(configKey).clone();
	}
	
	public static Object getConfig(String configKey, String[] property) {
		ConfigObject co = getConfig(configKey);
		if(co!=null) {
			return getConfig(co, property);
		}
		return null;
	}
	
	public static Object getConfig(ConfigObject co, String[] property) {
		int length = property.length;
		if(property!=null && length>0) {
			for(int i = 0; i < length; i++) {
				Object t = co.get(property[i]);
				if(t == null) {
					return null;
				} else if(i==length-1) {
					return t;
				} else if(t instanceof ConfigObject) {
					co = (ConfigObject)t;
				} else {
					return null;
				}
			}
		}
		return null;
		
		
//		StringBuilder sb = new StringBuilder();
//		for(String p : property) {
//			if(sb.length() != 0) {
//				sb.append(".");
//			}
//			sb.append(p);
//		}
//		String pros = sb.toString();
//		println configKey+" "+pros;
//		return MAP_CONFIG.get(configKey)."${pros}"();
	}

	private String getConfigKey(Path path) {
		return getConfigKey(path, 1);
	}

	private String getConfigKey(Path path, int len) {
		int pNameCount = path.getNameCount();
		StringBuffer sb = new StringBuffer();
		for (int i = len; i > 0 && i < pNameCount; i--) {
			String t = path.getName(pNameCount - i).toString();
			if (i == 1 && t.indexOf(".") > -1) {
				sb.append(t.substring(0, t.indexOf(".")));
			} else {
				sb.append(t).append(".");
			}
		}
		return sb.toString();
	}

}
