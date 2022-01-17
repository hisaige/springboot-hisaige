package com.hisaige.core.test.main;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class T implements Closeable {
	private static BufferedReader reader;
 
	public static void main(String[] args) {
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\上海一机一档摄像机查询.txt"))));
			System.out.println(reader.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	@Override
	public void close() throws IOException {
		reader.close();
	}
 
}
