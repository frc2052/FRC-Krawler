package com.team2052.frckrawler.io;

import java.io.*;
import java.lang.reflect.Array;

public class ObjectArrayInputStream extends ObjectInputStream {

	public ObjectArrayInputStream(InputStream input)
			throws StreamCorruptedException, IOException {
		super(input);
	}
	
	public <T> T[] readObjectArray(Class<T> c) throws IOException, ClassNotFoundException {
		
		int length = readInt();
		
		T[] arr = (T[]) Array.newInstance(c, length);
		
		for(int i = 0; i < length; i++) {
			
			arr[i] = (T)readObject();
		}
		
		return arr;
	}
}
