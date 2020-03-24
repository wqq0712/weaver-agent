package com.more.fw.weaver.agent.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PIDUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
	private static final String PID_FILENAME = "pid";
	private static String pid = null;
	static {
		loadPID();
	}
	
	/**
	 * 加载配置文件
	 */
	private static void loadPID() {
		String pidFilename = PID_FILENAME;
		logger.info("读取PID文件: {}", pidFilename);
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			in = PIDUtil.class.getClassLoader().getResourceAsStream(pidFilename);
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
	        String s = br.readLine();
	        StringBuilder sb = new StringBuilder();
	        while(s != null){
	        	sb.append(s);
	        	s = br.readLine();
	        }
	        pid = sb.toString().trim();
		} catch (IOException e) {
			logger.error("加载PID文件抛出异常", e);
		} finally {
			if(br != null) {
        		try {
        			br.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
        	}
			if(isr != null) {
        		try {
        			isr.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
        	}
			if(in != null) {
        		try {
        			in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
        	}
		}
	}

	public static String getPid() {
		return pid;
	}

	
}
