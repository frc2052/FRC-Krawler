package com.team2052.frckrawler.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectArrayOutputStream extends ObjectOutputStream {

	public ObjectArrayOutputStream(OutputStream output) throws IOException {
		
		super(output);
	}
	
	public void write(Object[] arr) throws IOException {
		
		write(arr.length);
		
		for(int i = 0; i < arr.length; i++) {
			
			super.writeObject(arr[i]);
		}
	}
}
