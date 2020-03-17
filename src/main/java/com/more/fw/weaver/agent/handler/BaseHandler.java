package com.more.fw.weaver.agent.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.util.GsonUtil;
import com.more.fw.weaver.agent.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler {
	private static Logger logger = LoggerFactory.getLogger(BaseHandler.class);
	
	//public static String encoding = System.getProperty("file.encoding");
	public static String encoding = "utf-8";
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		doHandle(exchange);
	}
	
	public abstract void doHandle(HttpExchange exchange) throws IOException;
	
	
	/**
	 * 获取查询参数列表Map
	 * 注意仅URL后面部分, Post请求不包括请求体的参数
	 * 注意这里为简易处理，不处理数组型的查询参数, 要支持的话需要Map里面再套一层List
	 * @param httpExchange
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	protected Map<String, String> getQueryParameterMap(HttpExchange httpExchange) throws UnsupportedEncodingException {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		
		String paramStr = httpExchange.getRequestURI().getQuery();
		if(StringUtil.isNotBlank(paramStr)) {
			String[] params = paramStr.split("&");
			for(String param : params) {
				String[] keyval = param.split("=");
				if(keyval == null || keyval.length > 2) {
					continue;
				} 
				String key = URLDecoder.decode(keyval[0], encoding);
				if(keyval.length == 1) {
					paramMap.put(key, null);
				} else {
					String value = URLDecoder.decode(keyval[1], encoding);
					paramMap.put(key,value);
				}
			}
		}
		return paramMap;
	}
	
	/**
	 * 获取请求体
	 * @param httpExchange
	 * @return
	 * @throws IOException
	 */
	protected String getRequestBody(HttpExchange httpExchange) throws IOException {
        String body = "";
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        //读取请求体
        try {
	        reader = new InputStreamReader(httpExchange.getRequestBody(), encoding);
	        bufferedReader = new BufferedReader(reader);
	        
	        StringBuilder requestBodyContent = new StringBuilder();
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            requestBodyContent.append(line);
	        }
	        body = requestBodyContent.toString();
        } finally {
        	if(bufferedReader != null) {
        		try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
        	}
        	if(reader != null) {
        		try {
        			reader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
        	}
        }
        return body;
    }
	
	/**
	 * 输出Json到响应中
	 * @param httpExchange
	 * @param responseObject
	 * @throws IOException
	 */
	protected void writeJsonToResponse(HttpExchange httpExchange, Object responseObject) throws IOException {
		OutputStream out = null;
		try {
			String responseJson = GsonUtil.beanToJson(responseObject);
			logger.debug("输出Json到Response, Json: {}", responseJson);
			byte[] responseContentByte = responseJson.getBytes(encoding);
			//设置响应头，需要在sendResponseHeaders方法之前设置
	        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
			//设置响应码和响应体长度，需要在getResponseBody方法之前调用
			httpExchange.sendResponseHeaders(200, responseContentByte.length);
			//不缓存
			//response.setHeader("Cache-Control", "no-cache");  
			//response.setHeader("Pragma", "no-cache");  
			//response.setDateHeader("Expires", 0);  
			
			out = httpExchange.getResponseBody();
	        out.write(responseContentByte);
	        out.flush();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	

}
