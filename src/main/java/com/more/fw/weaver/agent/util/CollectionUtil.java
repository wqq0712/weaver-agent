package com.more.fw.weaver.agent.util;

import java.util.List;
import java.util.Map;

public class CollectionUtil {
	
	public static boolean isEmpty(List<?> list) {
		if(list == null) {
			return true;
		}
		return list.isEmpty();
	}
	
	public static boolean isEmpty(Map<?, ?> map) {
		if(map == null) {
			return true;
		}
		return map.isEmpty();
	}
}
