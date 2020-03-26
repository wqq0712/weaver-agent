package com.more.fw.weaver.agent.handler;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.response.ResponseModel;
import com.more.fw.weaver.agent.response.ResponseStatus;
import com.more.fw.weaver.agent.server.MultipartFile;
import com.more.fw.weaver.agent.util.AppHomeUtil;
import com.more.fw.weaver.agent.util.ByteUtils;
import com.more.fw.weaver.agent.util.StringUtil;
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
			//应用编号
			String id = getParameterValue("id");
			//分片序号
			String indexStr = getParameterValue("index");
			int index = 0;
			if(StringUtil.isBlank(id) || StringUtil.isBlank(indexStr)) {
				writeJsonToResponse(exchange, ResponseModel.fail(ResponseStatus.BAD_REQUEST));
				return;
			}
			try {
				index = Integer.parseInt(indexStr);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				writeJsonToResponse(exchange, ResponseModel.fail(ResponseStatus.BAD_REQUEST));
				return;
			}
			MultipartFile file = getParameterFile("uploadFile");
			byte[] fileBytes = file.getFileContent();
			//构建保存文件名
			String fileName = id + "-" + index + ".part";
			String filePath = AppHomeUtil.getUploadDir() + fileName;
			
			//删除已存在文件
			File partFile = new File(filePath);
			if(partFile.exists() && partFile.isFile()) {
				partFile.delete();
			}
			
			ByteUtils.write2File(fileBytes, filePath);
			ResponseModel responseModel = ResponseModel.success("文件上传成功");
			writeJsonToResponse(exchange, responseModel);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			ResponseModel responseModel = ResponseModel.fail(ResponseStatus.SERVER_ERROR);
			writeJsonToResponse(exchange, responseModel);
		}
	}

}
