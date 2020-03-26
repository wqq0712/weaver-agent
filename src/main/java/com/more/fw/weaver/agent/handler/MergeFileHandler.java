package com.more.fw.weaver.agent.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.more.fw.weaver.agent.response.ResponseModel;
import com.more.fw.weaver.agent.response.ResponseStatus;
import com.more.fw.weaver.agent.util.AppHomeUtil;
import com.more.fw.weaver.agent.util.CollectionUtil;
import com.more.fw.weaver.agent.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 处理文件合并
 * @author qq.wang
 *
 */
public class MergeFileHandler extends BaseHandler {

	private static Logger logger = LoggerFactory.getLogger(MergeFileHandler.class);
	
	@Override
	public void doHandle(HttpExchange exchange) throws IOException {
		try {
			logger.debug("开始运行MergeFileHandler, 请求方式: {}", exchange.getRequestMethod());
			//应用编号
			String id = getParameterValue("id");
			if(StringUtil.isBlank(id)) {
				writeJsonToResponse(exchange, ResponseModel.fail(ResponseStatus.BAD_REQUEST));
				return;
			}
			String type = getParameterValue("type");
			String suffix = null;
			//暂时仅支持zip
			if(StringUtil.equalsIgnoreCase("zip", type)) {
				suffix = "zip";
			} else {
				suffix = "zip";
			}
			String uploadDir = AppHomeUtil.getUploadDir();
			File uploadDirFile = new File(uploadDir);
			if(!uploadDirFile.exists() || !uploadDirFile.isDirectory()) {
				writeJsonToResponse(exchange, new ResponseModel(500, "上传目录不存在"));
				return;
			}
			String[] partFilenames = listPartFilenames(uploadDirFile, id);
			if(CollectionUtil.isEmpty(partFilenames)) {
				writeJsonToResponse(exchange, new ResponseModel(500, String.format("找不到id:[%s]对应的分片文件", id)));
				return;
			}
			//按index排序
			Arrays.sort(partFilenames, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return getIndex(o1) - getIndex(o2);
				}
			});
			//文件合并
			mergeFiles(uploadDir, partFilenames, id + "." + suffix);
			writeJsonToResponse(exchange, ResponseModel.success("文件合并成功"));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			ResponseModel responseModel = ResponseModel.fail(ResponseStatus.SERVER_ERROR);
			writeJsonToResponse(exchange, responseModel);
		}
	}
	
	
	/**
	 * 执行文件合并
	 */
	private void mergeFiles(String uploadDir, String[] partFilenames, String mergeFilename) throws IOException {
		File file = new File(uploadDir + mergeFilename);
		if(file.exists() && file.isFile()) {
			file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			
			for (String partFilename : partFilenames) {
				File part = new File(uploadDir + partFilename);
				if(!file.exists()) {
					logger.error("{}不存在, 无法执行merge", file.getAbsolutePath());
					continue;
				}
				if(!file.isFile()) {
					logger.error("{}不是一个分片文件, 无法执行merge", file.getAbsolutePath());
					continue;
				}
				FileInputStream fis = null;
				try {
		            //读取切片文件
		            fis = new FileInputStream(part);
		            byte[] buff = new byte[1024];
		            int n = 0;
		            while ((n = fis.read(buff)) != -1) {
		            	fos.write(buff, 0, n);
		            }
				} finally {
					try {
						fis.close();
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
	        }
			fos.flush();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * 获取文件名中的index值
	 */
	private int getIndex(String filename) {
		if(StringUtil.isEmpty(filename)) {
			return -1;
		}
		int start = filename.lastIndexOf("-");
		if(start == -1) {
			logger.error("unexpect exception");
			return -1;
		}
		int end = filename.lastIndexOf(".part");
		if(end == -1) {
			logger.error("unexpect exception");
			return -1;
		}
		String indexStr = filename.substring(start + 1, end);
		try {
			return Integer.parseInt(indexStr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
		
	}
	
	/**
	 * 列出upload目录下符合  [id]-index.part 的所有文件名
	 */
	private String[] listPartFilenames(File uploadDirFile, String id) {
		//list方法只查找当前目录下的所有文件和文件夹, 不递归子目录
		String[] filterPartFilenames = uploadDirFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				//文件名规则: id + "-" + index + ".part";
				if(!StringUtil.startsWith(name, id + "-")) {
					return false;
				}
				if(!StringUtil.endsWith(name, ".part")) {
					return false;
				}
				//严谨的话这里可以再用正则进行进一步匹配
				return true;
			}
		});
		return filterPartFilenames;
	}

}
