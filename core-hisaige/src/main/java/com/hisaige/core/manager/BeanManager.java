package com.hisaige.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class BeanManager {

	private static final Logger logger = LoggerFactory.getLogger(BeanManager.class);

	@Autowired(required = false)
	private List<SystemManager> systemManagers;

	@PostConstruct
	public void init() {
		if (!CollectionUtils.isEmpty(systemManagers)) {
			systemManagers.forEach(systemManager -> {
				try {
					systemManager.init();
				} catch (Exception e) {
					logger.error("init systemManager error...", e);
				}
			});
		}
	}

	@PreDestroy
	public void unInit() {
		if (!CollectionUtils.isEmpty(systemManagers)) {
			systemManagers.forEach(systemManager -> {
				try {
					systemManager.unInit();
				} catch (Exception e) {
					logger.error("unInit systemManager error...", e);
				}
			});
		}
	}
}
