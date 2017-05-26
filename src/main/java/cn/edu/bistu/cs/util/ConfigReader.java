package cn.edu.bistu.cs.util;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigReader {
	
	private static HashMap<String, String> store = null;
	
	public static String getConfig(String key) {
		if (store == null) {
			 reload();
		}
		return store.get(key);
	}

	private static synchronized void reload() {
		store = new HashMap<String, String>();
		try {
			Configuration config = new PropertiesConfiguration("config.properties");
			@SuppressWarnings("unchecked")
			Iterator<String> keys = config.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				store.put(key, config.getString(key));
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(ConfigReader.getConfig("START_PAGE"));
	}
}
