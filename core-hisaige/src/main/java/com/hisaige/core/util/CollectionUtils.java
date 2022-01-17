package com.hisaige.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class CollectionUtils extends org.springframework.util.CollectionUtils {

	@SuppressWarnings("unchecked")
	public static <T> List<? super T> deepCopyList(List<? extends T> src) throws IOException, ClassNotFoundException {
//		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//				ObjectOutputStream out = new ObjectOutputStream(byteOut);) {
//			out.writeObject(src);
//			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
//					ObjectInputStream in = new ObjectInputStream(byteIn);) {
//				List<? super T> dest = (List<? super T>) in.readObject();
//				return dest;
//			}
//		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		List<? super T> dest = (List<? super T>) in.readObject();
		return dest;
	}

}
