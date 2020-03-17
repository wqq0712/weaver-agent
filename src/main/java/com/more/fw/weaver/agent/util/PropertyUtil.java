package com.more.fw.weaver.agent.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
	private static Properties props;
	private static final String CONFIG_FILENAME = "config.properties";
	static {
		loadProps();
	}

	/**
	 * 加载配置文件
	 */
	private static void loadProps() {
		String propFilename = CONFIG_FILENAME;
		logger.info("开始加载配置文件: {}", propFilename);
		props = new Properties();
		InputStream in = null;
		try {
			in = PropertyUtil.class.getClassLoader().getResourceAsStream(propFilename);
			props.load(in);
		} catch (FileNotFoundException e) {
			logger.error("未找到配置文件: {}", propFilename);
		} catch (IOException e) {
			logger.error("加载配置文件: {} 抛出异常", propFilename, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 根据Key获取属性值
	 * @param key
	 * @return
	 */
	public static String getStringProperty(String key) {
		if (props == null) {
			loadProps();
		}
		return props.getProperty(key);
	}

	
	/**
	 * 根据Key获取属性值，如果未找到，返回默认值
	 * @param key
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getStringProperty(String key, String defaultValue) {
		if (props == null) {
			loadProps();
		}
		return props.getProperty(key, defaultValue);
	}
	
	/**
	 * 根据Key获取属性值
	 * @param key
	 * @return
	 */
	public static Integer getIntProperty(String key) {
		if (props == null) {
			loadProps();
		}
		String strVal = props.getProperty(key);
		if(StringUtil.isBlank(strVal)) {
			return null;
		} else {
			Integer ret = null;
			try {
				ret = Integer.parseInt(strVal);
			} catch (NumberFormatException e) {
				logger.error("属性值[{}]无法转为integer", strVal, e);
				throw e;
			}
			return ret;
		}
	}
	
	/**
	 * 根据Key获取属性值
	 * @param key
	 * @return
	 */
	public static Integer getIntProperty(String key, int def) {
		if (props == null) {
			loadProps();
		}
		String strVal = props.getProperty(key);
		if(StringUtil.isBlank(strVal)) {
			return def;
		} else {
			Integer ret = def;
			try {
				ret = Integer.parseInt(strVal);
			} catch (NumberFormatException e) {
				logger.error("值[{}]无法转为integer", strVal, e);
			}
			return ret;
		}
	}
}