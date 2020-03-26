package com.more.fw.weaver.agent.util;

import java.io.File;

public class Constants {
	
	public static final String FILE_SEPARATOR = File.separator;
	
	public static final String CHARSET_UTF8 = "utf-8";
	
	public static final String KEY_SERVER_PORT = "server.port";
	
	public static final String KEY_SERVER_THREADPOOL_SIZE = "server.threadpool.size";
	
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	
	//http header
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	
	//http 文件上传相关
	public static final String HTTP_BOUNDARY = "boundary";
	public static final String HTTP_MULTIPART_FORM_DATA = "multipart/form-data";
	
	public static final String UPLOAD_DIR = "upload";
}
