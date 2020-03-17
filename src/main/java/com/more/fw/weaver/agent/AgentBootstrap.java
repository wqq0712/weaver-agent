package com.more.fw.weaver.agent;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.handler.PingHandler;
import com.more.fw.weaver.agent.util.Constants;
import com.more.fw.weaver.agent.util.PropertyUtil;
import com.sun.net.httpserver.HttpServer;

public class AgentBootstrap {
	
	private static Logger logger = LoggerFactory.getLogger(AgentBootstrap.class);
	
	/**
	 * 默认端口
	 */
	public static final int DEF_SERVER_PORT = 7050;
	
	/**
	 * HttpServer的线程池默认大小
	 */
	public static final int DEF_THREADPOOL_SIZE = 3;
	
	public static void main(String[] args) {
		logger.info("正在开始启动Weaver-Agent服务.");
		try {
			int port = PropertyUtil.getIntProperty(Constants.KEY_SERVER_PORT, DEF_SERVER_PORT);
			//创建一个HttpServer实例，并绑定端口号
	        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
	        //创建HttpContext，将对应请求映射到相应处理器
	        httpServer.createContext("/ping", new PingHandler());
	        
	        // HttpServer默认为内部的DefaultExecutor, 实质是复用当前的线程
	        // 这里设置服务器的线程池对象
	        int threadPoolSize = PropertyUtil.getIntProperty(Constants.KEY_SERVER_THREADPOOL_SIZE, DEF_THREADPOOL_SIZE);
	        ExecutorService es = Executors.newFixedThreadPool(threadPoolSize);
	        httpServer.setExecutor(es);
	        //启动服务器
	        httpServer.start();
			logger.info("Weaver-Agent启动成功, 开放端口: {}.", port);
		} catch (Exception e) {
			logger.error("Weaver-Agent服务启动失败", e);
		}
	}
}
