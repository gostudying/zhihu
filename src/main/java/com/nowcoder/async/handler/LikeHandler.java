package com.nowcoder.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

@Component
public class LikeHandler implements EventHandler {
	@Autowired
	MessageService messageService;

	@Autowired
	UserService userService;

	@Override
	public void doHandle(EventModel model) {
		Message message = new Message();
		message.setFromId(WendaUtil.SYSTEM_USERID);
		message.setToId(model.getEntityOwnerId());
		message.setCreatedDate(new Date());
		User user = userService.getUser(model.getActorId());
		message.setContent(
				"用户" + user.getName() + "赞了你的评论，http://127.0.0.1:8080/question/" + model.getExt("questionId"));
		message.setHasRead(0);
		messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventType() {
		return Arrays.asList(EventType.LIKE);
	}

}
