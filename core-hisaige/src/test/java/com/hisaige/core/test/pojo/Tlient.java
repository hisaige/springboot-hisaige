package com.hisaige.core.test.pojo;

import java.io.Closeable;
import java.io.IOException;

public class Tlient implements Closeable {

	@Override
	public void close() throws IOException {
		System.out.println("Tlient is close...");
	}
	public Tlient() {
		System.out.println("Tlient is open...");
	}
	public void method() {
		System.out.println("Tlient method...");
	}

}
