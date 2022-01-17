package com.hisaige.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hisaige.core.httpclient.HttpOperation;
import com.hisaige.core.httpclient.common.HttpRes;
import org.junit.Test;

import com.hisaige.core.test.pojo.Person;
import com.hisaige.core.util.CollectionUtils;
import com.hisaige.core.util.HttpClientUtil;

public class CommonTest {

	public CommonTest() throws IOException {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test1() throws ClassNotFoundException, IOException {
		int sum = 0;
		for (int j = 0; j < 30; j++) {
			long startTime = System.currentTimeMillis();
			List<Person> persons = new ArrayList<>();
			for (int i = 0; i < 5000; i++) {
				Person person = new Person(i, "张三" + String.valueOf(i), "海南省", "男", 18);
				persons.add(person);
			}
			List<Person> persons2 = null;
			try {
				persons2 = (List<Person>) CollectionUtils.deepCopyList(persons);
			}catch (Exception e) {
				e.printStackTrace();
			}
			persons2.get(0).setName("李四");
			persons.get(0).setName("王五");
//			System.out.println(persons2.get(0));
//			System.out.println(persons.get(0));
			System.out.println(System.currentTimeMillis() - startTime);
			sum += System.currentTimeMillis() - startTime;
		}
		System.out.println(sum + "-------");
	}

	@Test
	public void test2() throws IOException {
		try (HttpClientUtil httpClientUtil = new HttpClientUtil("localhost", 8888);) {
			System.out.println(httpClientUtil.doPost("http://10.82.27.80", null, null));
		}
	}

	@Test
	public void test3() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ")
			.append("c.camera_id AS infinovaId, ")
			.append("c.pseudo AS pseudo, ")
			.append("c.camera_type AS cameraType, ")
			.append("o1.org_code AS cameraOrgCode, ")
			.append("o1.org_name AS orgName, ")
			.append("r1.enable_ AS enableCamera, ")
			.append("d.device_name AS deviceName, ")
			.append("d.port AS devicePort, ")
			.append("d.source_share_id AS deviceShareId, ")
			.append("d.device_code AS deviceOrgCode, ")
			.append("o2.org_name AS deviceOrgName, ")
			.append("d.protocol AS protocol, ")
			.append("r2.enable_ AS enableDevice ")
			.append("FROM ")
			.append("camera c,device d,resource_zonning r1,resource_zonning r2,org_node o1,org_node o2 ")
			.append("WHERE ")
			.append("c.device_id = d.device_id ")
			.append("AND r1.resource_id = c.camera_id ")
			.append("AND r2.resource_id = d.device_id ")
			.append("AND o1.org_id = r1.org_id ")
			.append("AND o2.org_id = r2.org_id ")
			.append("AND d.device_type != 'platform' ");
		String sql = sb.toString();
		System.out.println(sql);
	}

	//需要运行testApplication
	@Test
	public void test4() throws IOException, InterruptedException {
//		for (int i = 0; i < 1000; i++) {
//			new Thread(()->{
//				HttpRes httpRes = null;
//				try {
//					httpRes = HttpOperation.doGet("http://127.0.0.1:9002/test1", null);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				System.out.println(httpRes);
//			}, "thread-" + i).start();
//		}
//		TimeUnit.SECONDS.sleep(12);
//		HttpRes httpRes = HttpOperation.heartbeatGet("http://127.0.0.1:9002/test1", null);
		HttpRes httpRes = HttpOperation.doGet("http://127.0.0.1:9002/test1", null);
		System.out.println(httpRes);
	}


}
