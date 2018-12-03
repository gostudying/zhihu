package com.nowcoder.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
	// EventType和EventHandler是多对多的关系，config是关联一个EventType对应的多个EventHandler
	private Map<EventType, List<EventHandler>> config = new HashMap<>();
	private ApplicationContext applicationContext;
	
	@Autowired
	JedisAdapter jedisAdapter;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 生成所有类型为EventHandler（包括子类）的对象
		Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
		if (beans != null) {
			for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
				// 找到EventHandler对应的每个EventType
				List<EventType> eventTypes = entry.getValue().getSupportEventType();
				// 将该EventHandler加入其对应的每个EventType
				for (EventType eventType : eventTypes) {
					if (!config.containsKey(eventType)) {
						config.put(eventType, new ArrayList<EventHandler>());
					}
					config.get(eventType).add(entry.getValue());
				}
			}
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					String key = RedisKeyUtil.getEventQueueKey();
					// 0表示队列为空时，线程堵塞，返回值是一个list，例如{key， event}
					List<String> events = jedisAdapter.brpop(0, key);
					for (String message : events) {
						if (message.equals(key)) {
							continue;
						}
						EventModel eventModel = JSON.parseObject(message, EventModel.class);
						if (!config.containsKey(eventModel.getType())) {
							logger.error("不能识别的事件");
							continue;
						}
						// 找到eventModel对应的EventHandler，分别处理该事件
						for (EventHandler handler : config.get(eventModel.getType())) {
							handler.doHandle(eventModel);
						}
					}
				}
			}
		});
		thread.start();
	}

}
