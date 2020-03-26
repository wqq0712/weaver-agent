package com.more.fw.weaver.agent;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.more.fw.weaver.agent.discover.AppDiscover;
import com.more.fw.weaver.agent.handler.ConfigHandler;
import com.more.fw.weaver.agent.handler.DiscoverHandler;
import com.more.fw.weaver.agent.handler.InstallHandler;
import com.more.fw.weaver.agent.handler.JoinHandler;
import com.more.fw.weaver.agent.handler.MergeFileHandler;
import com.more.fw.weaver.agent.handler.PingHandler;
import com.more.fw.weaver.agent.handler.PortCheckHandler;
import com.more.fw.weaver.agent.handler.ShutdownHandler;
import com.more.fw.weaver.agent.handler.StartHandler;
import com.more.fw.weaver.agent.handler.StopHandler;
import com.more.fw.weaver.agent.handler.UploadFileHandler;
import com.more.fw.weaver.agent.handler.UploadFilePartHandler;
import com.more.fw.weaver.agent.util.AppHomeUtil;
import com.more.fw.weaver.agent.util.Constants;
import com.more.fw.weaver.agent.util.FileUtil;
import com.more.fw.weaver.agent.util.PIDUtil;
import com.more.fw.weaver.agent.util.PropertyUtil;
import com.more.fw.weaver.agent.util.StringUtil;
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
	public static final int DEF_THREADPOOL_SIZE = 5;
	
	public static void main(String[] args) {
		logger.info("正在开始启动Weaver-Agent服务.");
		try {
			int port = PropertyUtil.getIntProperty(Constants.KEY_SERVER_PORT, DEF_SERVER_PORT);
			String pid = PIDUtil.getPid();
			if(StringUtil.isBlank(pid)) {
				throw new IllegalStateException("无法获取Agent归属PID");
			}
			// 扫描检测已部署的服务, 应用启动参数要求传入扫描路径参数
			// 主线程执行, 如果要加快效率，可以开启新线程执行，但是要考虑需要在客户端请求获取发现资源前完成扫描工作
			String scanPath = null;
            if (args.length > 0) {
            	scanPath = args[0];
            }
            if(StringUtil.isBlank(scanPath)) {
            	logger.info("未传入应用发现路径，不执行应用发现");
            } else {
            	logger.info("将执行应用发现，扫描路径：{}", scanPath);
            	if(!FileUtil.isDirectory(scanPath)) {
            		throw new IllegalArgumentException("应用发现扫描路径不是目录");
            	}
            	AppDiscover discover = new AppDiscover();
    			discover.scan(scanPath);
            }
			// 创建一个HttpServer实例，并绑定端口号
	        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
	        // 注册接口地址
	        registerEndpoints(httpServer);
	        // HttpServer默认为内部的DefaultExecutor, 实质是复用当前的线程
	        // 设置服务器处理的线程池对象
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
	
	private static void registerEndpoints(HttpServer httpServer) {
		// 创建HttpContext，将对应请求映射到相应处理器
        httpServer.createContext("/ping", new PingHandler());
        //端口检测
        httpServer.createContext("/portCheck", new PortCheckHandler());
        //应用发现
        httpServer.createContext("/discover", new DiscoverHandler());
        //应用加入管控范围
        httpServer.createContext("/join", new JoinHandler());
        //关闭Agent服务
        httpServer.createContext("/shutdown", new ShutdownHandler());
        //文件上传
        httpServer.createContext("/uploadFile", new UploadFileHandler());
        //切片文件上传
        httpServer.createContext("/uploadFilePart", new UploadFilePartHandler());
        //合并切片文件
        httpServer.createContext("/mergeFile", new MergeFileHandler());
        //安装应用
        httpServer.createContext("/install", new InstallHandler());
        //配置应用
        httpServer.createContext("/config", new ConfigHandler());
        //启动应用
        httpServer.createContext("/start", new StartHandler());
        //停止应用
        httpServer.createContext("/stop", new StopHandler());
        
	}
}
