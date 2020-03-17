package com.more.fw.weaver.agent.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.response.ResultModel;
import com.sun.net.httpserver.HttpExchange;

public class PingHandler extends BaseHandler {

	private static Logger logger = LoggerFactory.getLogger(PingHandler.class);
	
	@Override
	public void doHandle(HttpExchange exchange) throws IOException {
		logger.debug("开始运行PingHandler, 请求方式: {}", exchange.getRequestMethod());
		
		try {
			//获取请求参数
			Map<String, String> map = getQueryParameterMap(exchange);
			logger.debug("请求参数: {}", map.toString());
			
			//获取请求体
			String requestBody = getRequestBody(exchange);
			logger.debug("请求体: {}", requestBody);
			
			Map<String, String> map222 = new HashMap<>();
			map222.put("测试Key1", "value1");
			map222.put("测试Key2", "value2");
			ResultModel<?> result = ResultModel.success("成功返回", map222);
			writeJsonToResponse(exchange, result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	
	
	

}
