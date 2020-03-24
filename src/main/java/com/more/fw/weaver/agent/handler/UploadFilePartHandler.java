package com.more.fw.weaver.agent.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.response.ResponseModel;
import com.more.fw.weaver.agent.response.ResponseStatus;
import com.more.fw.weaver.agent.server.MultipartFile;
import com.more.fw.weaver.agent.util.ByteUtils;
import com.sun.net.httpserver.HttpExchange;

/**
 * 处理文件分片上传
 * @author qq.wang
 *
 */
public class UploadFilePartHandler extends BaseHandler {

	private static Logger logger = LoggerFactory.getLogger(UploadFilePartHandler.class);
	
	@Override
	public void doHandle(HttpExchange exchange) throws IOException {
		try {
			logger.debug("开始运行UploadFilePartHandler, 请求方式: {}", exchange.getRequestMethod());
			String filepath = "E:\\test\\weaver\\ocr22.zip";
			MultipartFile file = getParameterFile("deployFilePart");
			byte[] fileBytes = file.getFileContent();
			ByteUtils.write2File(fileBytes, filepath);
			
			ResponseModel responseModel = ResponseModel.success("文件上传成功");
			writeJsonToResponse(exchange, responseModel);
		} catch(Exception e) {
			ResponseModel responseModel = ResponseModel.fail(ResponseStatus.SERVER_ERROR);
			writeJsonToResponse(exchange, responseModel);
		}
	}

}
