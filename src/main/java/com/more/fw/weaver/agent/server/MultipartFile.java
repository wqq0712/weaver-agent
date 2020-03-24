package com.more.fw.weaver.agent.server;

import java.util.Arrays;

import com.more.fw.weaver.agent.util.StringUtil;

public class MultipartFile {

	private String paramName;
	
	private String fileName;
	
	private byte[] fileContent;
	
	private String suffix;
	
	private String contentType;
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.suffix=getSuffix(fileName);
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	private String getSuffix(String fileName) {
		if (StringUtil.isBlank(fileName)) {
			return null;
		}
		String[] strs = fileName.split("\\.");
		return strs[strs.length - 1].toLowerCase();
	}

	@Override
	public String toString() {
		return "MultipartFile [paramName=" + paramName + ", fileName=" + fileName + ", fileContext="
				+ Arrays.toString(fileContent) + ", suffix=" + suffix + ", contextType=" + contentType + "]";
	}

	
}
