package com.more.fw.weaver.agent.util;

public class AppHomeUtil {
	/**
	 * 获取程序运行的目录
	 * @return
	 */
	public static String getExecuteDir() {
		String dir = System.getProperty("user.dir");
		return dir;
		
	}
	
	/**
	 * 获取上传目录
	 * @return
	 */
	public static String getUploadDir() {
		return getExecuteDir() + Constants.FILE_SEPARATOR + Constants.UPLOAD_DIR + Constants.FILE_SEPARATOR;
	}
}
