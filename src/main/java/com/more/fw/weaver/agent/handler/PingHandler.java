package com.more.fw.weaver.agent.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.response.ResponseModel;
import com.sun.net.httpserver.HttpExchange;

/**
 * Ping接口
 * @author qq.wang
 *
 */
public class PingHandler extends BaseHandler {

	private static Logger logger = LoggerFactory.getLogger(PingHandler.class);
	
	@Override
	public void doHandle(HttpExchange exchange) throws IOException {
		logger.debug("开始运行PingHandler, 请求方式: {}", exchange.getRequestMethod());
		ResponseModel responseModel = ResponseModel.success("ping成功");
		writeJsonToResponse(exchange, responseModel);
	}
}
