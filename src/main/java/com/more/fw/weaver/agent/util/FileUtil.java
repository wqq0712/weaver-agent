package com.more.fw.weaver.agent.util;

import java.io.File;

public class FileUtil {
	
	public static boolean isDirectory(String filepath) {
		if (StringUtil.isBlank(filepath)) {
            return false;
        }
		File f = new File(filepath);
		return f.isDirectory();
	}
}
