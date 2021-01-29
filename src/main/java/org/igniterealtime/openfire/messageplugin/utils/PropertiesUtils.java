package org.igniterealtime.openfire.messageplugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

	/**
	 * 按key获取值
	 * 
	 * @param key
	 * @return
	 */
	public static String readProperty(String name, String key) {
		String value = null;
		InputStream is = PropertiesUtils.class.getResourceAsStream(name);
		if(is != null) {
			try {
				Properties p = new Properties();
				p.load(is);
				value = p.getProperty(key);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	/**
	 * 获取整个配置信息
	 * 
	 * @return
	 */
	public static Properties getProperties(String name) {
		Properties p = new Properties();
		InputStream is = PropertiesUtils.class.getResourceAsStream(name);
		if(is != null) {
			try {
				p.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return p;
	}

}