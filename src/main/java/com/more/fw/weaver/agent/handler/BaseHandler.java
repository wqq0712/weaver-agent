package com.more.fw.weaver.agent.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.response.ResponseModel;
import com.more.fw.weaver.agent.response.ResponseStatus;
import com.more.fw.weaver.agent.server.MultipartFile;
import com.more.fw.weaver.agent.util.ByteUtils;
import com.more.fw.weaver.agent.util.CollectionUtil;
import com.more.fw.weaver.agent.util.Constants;
import com.more.fw.weaver.agent.util.EncryptUtil;
import com.more.fw.weaver.agent.util.GsonUtil;
import com.more.fw.weaver.agent.util.PIDUtil;
import com.more.fw.weaver.agent.util.ParameterUtil;
import com.more.fw.weaver.agent.util.StringUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * 请求处理的基类，封装常用的Http请求相关方法
 * 
 * @author qq.wang
 *
 */
public abstract class BaseHandler implements HttpHandler {
	private static Logger logger = LoggerFactory.getLogger(BaseHandler.class);
	
	//public static String encoding = System.getProperty("file.encoding");
	public static String encoding = Constants.CHARSET_UTF8;
	
	private Map<String, List<Object>> parameterMap;
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//解析请求参数
		this.parameterMap = parseHttpParameters(exchange);
		//校验签名
		boolean valid = validateSign();
		if(valid) {
			doHandle(exchange);
		} else {
			ResponseModel responseModel = ResponseModel.fail(ResponseStatus.UNAUTHORIZED);
			writeJsonToResponse(exchange, responseModel);
		}
	}
	
	/**
	 * 根据参数名返回参数值，如果参数名对应多个参数值，仅返回第一个
	 * @param name
	 * @return
	 */
	public String getParameterValue(String name) {
		Object obj = getParameter(name);
		return (String)obj;
	}
	
	public MultipartFile getParameterFile(String name) {
		Object obj = getParameter(name);
		return (MultipartFile)obj;
	}
	
	/**
	 * 根据参数名返回参数值，如果参数名对应多个参数值，仅返回第一个
	 * @param name
	 * @return
	 */
	private Object getParameter(String name) {
		List<Object> list = parameterMap.get(name);
		if(CollectionUtil.isEmpty(list)) {
			return null;
		} else {
			Object obj = list.get(0);
			if(obj instanceof MultipartFile) {
				MultipartFile mf = (MultipartFile)obj;
				String contentType = mf.getContentType();
				try {
					if(StringUtil.equals(Constants.CONTENT_TYPE_TEXT_PLAIN, contentType)) {
						return new String(mf.getFileContent(), Constants.CHARSET_UTF8);
					} else {
						return mf;
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					return null;
				}
			} else {
				//如果是对象类型，直接转为String
				return list.get(0).toString();
			}
		}
	}
	
	/**
	 * 根据参数名返回参数值列表
	 * @param name
	 * @return
	 */
	public List<Object> getParameterValues(String name) {
		return parameterMap.get(name);
	}
	
	/**
	 * 留待子类实现
	 * @param exchange
	 * @throws IOException
	 */
	public abstract void doHandle(HttpExchange exchange) throws IOException;
	
	/**
	 *  校验签名(鉴权)
	 * @return
	 */
	private boolean validateSign() {
		//签名
		String sign = getParameterValue("sign");
		//随机字符串
		String rand = getParameterValue("rand");
		if(StringUtil.isBlank(rand)) {
			logger.warn("rand请求参数为空");
			return false;
		}
		if(StringUtil.isBlank(sign)) {
			logger.warn("sign请求参数为空");
			return false;
		}
		String pid = PIDUtil.getPid();
		String temp = rand + pid;
		boolean valid = false;
		try {
			valid = EncryptUtil.checkPassword(sign, temp);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return valid;
	}
	
	/**
	 * 获取请求头
	 * @param httpExchange
	 * @param key
	 * @return
	 */
	public String getHeader(HttpExchange httpExchange, String key){
		Headers headers = httpExchange.getRequestHeaders();
		if(!headers.containsKey(key)) {
			return null;
		}
		List<String> list = headers.get(key);
		if(CollectionUtil.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	/**
	 * 判断是否文件上传请求
	 * @param httpExchange
	 * @return
	 */
	public boolean isMultipartRequest(HttpExchange httpExchange) {
		String contentType = getHeader(httpExchange, Constants.HEADER_CONTENT_TYPE);
		if(StringUtil.isBlank(contentType)) {
			return false;
		} else {
			if(StringUtil.containsIgnoreCase(contentType, Constants.HTTP_MULTIPART_FORM_DATA)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 是否包含对应key的请求头
	 * @param httpExchange
	 * @param key
	 * @return
	 */
	public boolean hasContainHeader(HttpExchange httpExchange, String key){
		Headers headers = httpExchange.getRequestHeaders();
		return headers.containsKey(key);
	}
	
	
	
	/**
	 * 解析请求参数，包括URL请求参数和消息中的请求参数
	 * @param httpExchange
	 * @throws IOException 
	 */
	private Map<String, List<Object>> parseHttpParameters(HttpExchange httpExchange) throws IOException{
		//先处理Http Url参数
		String queryString = httpExchange.getRequestURI().getQuery();
		
		Map<String, List<Object>> params = ParameterUtil.buildGeneralParams(queryString);
		if (isMultipartRequest(httpExchange)) {
			String line = getHeader(httpExchange, Constants.HEADER_CONTENT_TYPE);
			String[] dabbles = line.split(";");
			String boundary = "";
			for (String dabble : dabbles) {
				int index = dabble.indexOf("=");
				if (index < 1 || index > dabble.length()) {
					continue;
				}
				String name = dabble.substring(0, dabble.indexOf("=")).trim();
				String value = dabble.substring(dabble.indexOf("=") + 1);
				if (name.equals(Constants.HTTP_BOUNDARY)) {
					boundary = value;
				}
			}
			InputStream inputStream = httpExchange.getRequestBody();
			String contentLengthStr = getHeader(httpExchange, Constants.HEADER_CONTENT_LENGTH);
			
			int length = -1;
			if(StringUtil.isNotBlank(contentLengthStr)) {
				try {
					length = Integer.parseInt(contentLengthStr);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			} 
			byte[] data = null;
			if(length == -1) {
				data = ByteUtils.getBytes(inputStream);
			} else {
				data = ByteUtils.getBytes(inputStream, length);
			}
			Map<String, List<Object>> paramMap = ParameterUtil.buildMultipartParams(data, boundary);
			params = mergeParaMap(params, paramMap);
		} else {
			String postContent = getRequestBody(httpExchange);
			if (StringUtil.isNotEmpty(postContent)) {
				Map<String, List<Object>> paramMap = ParameterUtil.buildGeneralParams(postContent);
				params = mergeParaMap(params, paramMap);
			}
		}
		return params;
	}
	
	/**
	 * 合并参数map
	 * @param paraMap1
	 * @param paraMap2
	 * @return
	 */
	private Map<String, List<Object>> mergeParaMap(Map<String, List<Object>> paraMap1,
			Map<String, List<Object>> paraMap2) {
		if (CollectionUtil.isEmpty(paraMap1)) {
			return paraMap2;
		}
		if (CollectionUtil.isEmpty(paraMap2)) {
			return paraMap1;
		}
		for (String key : paraMap1.keySet()) {
			if (!paraMap2.containsKey(key)) {
				paraMap2.put(key, paraMap1.get(key));
				continue;
			}
			paraMap2.get(key).addAll(paraMap1.get(key));
		}
		return paraMap2;
	}
	
	/**
	 * 获取请求体
	 * @param httpExchange
	 * @return
	 * @throws IOException
	 */
	public String getRequestBody(HttpExchange httpExchange) throws IOException {
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
	protected void writeJsonToResponse(HttpExchange httpExchange, Object responseObject){
		OutputStream out = null;
		try {
			String responseJson = GsonUtil.beanToJson(responseObject);
			logger.debug("输出Json到Response, Json: {}", responseJson);
			byte[] responseContentByte = responseJson.getBytes(encoding);
			//设置响应头，需要在sendResponseHeaders方法之前设置
	        httpExchange.getResponseHeaders().add(Constants.HEADER_CONTENT_TYPE, "application/json;charset=UTF-8");
			//设置响应码和响应体长度，需要在getResponseBody方法之前调用
			httpExchange.sendResponseHeaders(200, responseContentByte.length);
			//不缓存
			//response.setHeader("Cache-Control", "no-cache");  
			//response.setHeader("Pragma", "no-cache");  
			//response.setDateHeader("Expires", 0);  
			
			out = httpExchange.getResponseBody();
	        out.write(responseContentByte);
	        out.flush();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
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
