package com.hisaige.core.component;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestBean {

	private static final Logger logger = LoggerFactory.getLogger(TestBean.class);
	
	
	@PostConstruct
	private void test() {
		logger.debug("debug....");
		logger.info("info...");
		logger.warn("warn...");
		logger.error("error...");
	}
}
