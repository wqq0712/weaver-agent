package com.more.fw.weaver.agent.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtils {
	
	private static Logger logger = LoggerFactory.getLogger(ByteUtils.class);
	
	public static Integer MAX_HEADER_LENGTH = 8192;

	public static byte[] readLine(InputStream inputStream) throws IOException {
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			int chr = -1;
			while ((chr = inputStream.read()) != -1) {
				swapStream.write(chr);
				if (chr == 10) {
					break;
				}
			}
			return swapStream.toByteArray();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			swapStream.close();
		}
		return null;
	}

	public static String readLineString(InputStream inputStream) {
		try {
			byte[] data = readLine(inputStream);
			if (data == null || data.length == 0) {
				return null;
			}
			return new String(data, Constants.CHARSET_UTF8);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static byte[] getBytes(InputStream ins) {
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = ins.read(buff, 0, 1024)) != -1) {
				swapStream.write(buff, 0, rc);
			}
			return swapStream.toByteArray();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				swapStream.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/*
	public static byte[] getBytes(SocketChannel channel, Integer length) {
		if (length < 1) {
			return null;
		}
		int company = 1024;
		if (length < company) {
			company = length;
		}
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			ByteBuffer buff = ByteBuffer.allocate(company);
			int totalReadLength = 0;
			while (totalReadLength < length) {
				try {
					int rcLength = channel.read(buff);
					if (rcLength == 0) {
						TimeUnit.MICROSECONDS.sleep(1);
					}
					totalReadLength += rcLength;
					buff.flip();
					byte[] data = new byte[buff.remaining()];
					buff.get(data);
					swapStream.write(data);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					buff.flip();
					buff.clear();
				}

			}
			return swapStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				swapStream.close();
			} catch (IOException e) {
			}
		}
	}
	*/
	public static byte[] getBytes(InputStream inputStream, Integer length) {
		if (length < 1) {
			return null;
		}
		if (inputStream == null) {
			return null;
		}
		int company = MAX_HEADER_LENGTH;
		if (length < company) {
			company = length;
		}
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[company];
			int totalReadLength = 0;
			while (totalReadLength < length) {
				int rcLength = inputStream.read(buff);
				if (rcLength == 0) {
					TimeUnit.MICROSECONDS.sleep(1);
					break;
				}
				totalReadLength += rcLength;
				swapStream.write(buff, 0, rcLength);
				/*
				if (rcLength < buff.length) {
					break;
				}
				*/
			}
			return swapStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				swapStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void write2File(byte[] byteArr, String filepath) throws IOException {
		if(StringUtil.isBlank(filepath)) {
			throw new IllegalArgumentException("参数不能为空");
		}
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filepath));
			bos = new BufferedOutputStream(fos);
			bos.write(byteArr);
			bos.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if(bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			try {
				if(fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	

}
