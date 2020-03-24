package com.more.fw.weaver.agent.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.more.fw.weaver.agent.server.MultipartFile;

public class ParameterUtil {
	/**
	 * 简单装载参数
	 * 
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static Map<String, List<Object>> buildGeneralParams(String queryString) throws UnsupportedEncodingException {
		if (StringUtil.isBlank(queryString)) {
			return new HashMap<String, List<Object>>();
		}
		String[] lines = queryString.split("&");
		Map<String, List<Object>> params = new HashMap<String, List<Object>>();
		for (String line : lines) {
			try {
				int index = line.indexOf("=");
				if (index < 1 || index == line.length() - 1) {
					continue;
				}
				String paramName = line.substring(0, index);
				String paramValue = URLDecoder.decode(line.substring(index + 1), Constants.CHARSET_UTF8);
				if (!params.containsKey(paramName)) {
					List<Object> paramValues = new ArrayList<Object>();
					params.put(paramName, paramValues);
				}
				params.get(paramName).add(paramValue);
			} catch (UnsupportedEncodingException e) {
				throw e;
			}
		}
		return params;
	}
	
	/**
	 * 解析文件上传的请求体
	 * 
	 * @param data
	 * @param boundary
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IOException
	 */
	public static Map<String, List<Object>> buildMultipartParams(byte[] data, String boundary) throws UnsupportedEncodingException {
		if (data == null || data.length == 0) {
			return new HashMap<String, List<Object>>();
		}
		Map<String, List<Object>> resultMap = new HashMap<String, List<Object>>();
		try {
			String context = new String(data, "ISO-8859-1");
			String boundaryTag = "--" + boundary;
			String[] paramContents = context.split(boundaryTag);
			for (String paramContent : paramContents) {
				MultipartFile multipartFile = buildMultipartFile(paramContent);
				if (multipartFile == null) {
					continue;
				}
				if (!resultMap.containsKey(multipartFile.getParamName())) {
					List<Object> files = new ArrayList<Object>();
					resultMap.put(multipartFile.getParamName(), files);
				}
				resultMap.get(multipartFile.getParamName()).add(multipartFile);
			}
		} catch (UnsupportedEncodingException e) {
			throw e;
		}
		return resultMap;
	}

	private static MultipartFile buildMultipartFile(String paramContent) throws UnsupportedEncodingException {
		if (StringUtil.isBlank(paramContent)) {
			return null;
		}
		ByteArrayInputStream inputStream = null;
		try {
			//会包含/r/n, 需要去除，不能单纯的使用 trim(), 因为某些特殊的文件格式可能最后会有空字符,比如zip,rar
			StringBuilder sb = new StringBuilder(paramContent);
			int len = sb.length();
			if (len >= 2 && sb.charAt(len - 2) == '\r') {
				sb.setLength(len - 2);  // cut \r\n
				inputStream = new ByteArrayInputStream(sb.toString().getBytes("ISO-8859-1"));
			}
			else if (len >= 1 && sb.charAt(len - 1) == '\n') {
				sb.setLength(len - 1);  // cut \n
				inputStream = new ByteArrayInputStream(sb.toString().getBytes("ISO-8859-1"));
			} else {
				inputStream = new ByteArrayInputStream(paramContent.getBytes("ISO-8859-1"));
			}
			
			String line = ByteUtils.readLineString(inputStream).trim();
			while (StringUtil.isBlank(line)) {
				line = ByteUtils.readLineString(inputStream).trim();
			}
			String contentType = Constants.CONTENT_TYPE_TEXT_PLAIN;
			Map<String, String> buildMap = buildParaMap(line);
			if (buildMap == null || buildMap.isEmpty()) {
				return null;
			}
			String paramName = buildMap.get("name");
			if (StringUtil.isBlank(paramName)) {
				return null;
			}
			line = ByteUtils.readLineString(inputStream).trim();
			if (line.contains("Content-Type")) {
				contentType = line.substring(line.indexOf(":") + 1).trim();
			}
			while (!StringUtil.isBlank(line)) {
				line = ByteUtils.readLineString(inputStream).trim();
			}
			byte[] value = ByteUtils.getBytes(inputStream);
			MultipartFile multipartFile = new MultipartFile();
			multipartFile.setContentType(contentType);
			multipartFile.setFileContent(value);
			multipartFile.setParamName(buildMap.get("name"));
			multipartFile.setFileName(buildMap.get("filename"));
			return multipartFile;
		} catch (UnsupportedEncodingException e) {
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Map<String, String> buildParaMap(String context) {
		if (context.contains(":")) {
			context = context.substring(context.indexOf(":") + 1);
		}
		String[] lines = context.split("; ");
		Map<String, String> paraMap = new HashMap<String, String>();
		for (String line : lines) {
			if (!line.contains("=")) {
				continue;
			}
			String name = line.substring(0, line.indexOf("=")).trim();
			String value = line.substring(line.indexOf("=") + 1).replace("\"", "").trim();
			if (StringUtil.isBlank(name)) {
				continue;
			}
			paraMap.put(name, value);
		}
		if (paraMap.isEmpty()) {
			return null;
		}
		return paraMap;
	}

	public static void main(String[] args) {
		int a = '\r';
		System.out.println(a);
	}
}
