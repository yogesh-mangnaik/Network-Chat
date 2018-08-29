package com.mangnaik.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifiers {
	
	private static List<Integer> ids = new ArrayList<Integer>();
	private static final int RANGE = 10000;
	
	private static int index = 0;
	
	static {
		for(int i = 0;i < RANGE ; i++){
			ids.add(i);
		}
		Collections.shuffle(ids);
	}
	
	public static int getIdentifier(){
		return ids.get(index++);
	}
}
