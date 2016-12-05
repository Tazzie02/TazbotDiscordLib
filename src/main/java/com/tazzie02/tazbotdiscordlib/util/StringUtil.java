package com.tazzie02.tazbotdiscordlib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
	
	public static String[] removeIndex(String[] args, int index) {
		List<String> list = new ArrayList<>(Arrays.asList(args));
		list.remove(index);
		
		String[] newArgs = new String[list.size()];
		return list.toArray(newArgs);
	}

}
