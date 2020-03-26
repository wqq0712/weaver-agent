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
	
	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}
	
	public static boolean isEmpty(Object[] objs) {
		if(objs == null || objs.length == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(Object[] objs) {
		return !isEmpty(objs);
	}
	
	public static boolean isEmpty(Map<?, ?> map) {
		if(map == null) {
			return true;
		}
		return map.isEmpty();
	}
	
	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}
	
}
