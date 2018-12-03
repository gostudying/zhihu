package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;

@Service
public class MessageService {
	@Autowired
	MessageDAO messageDAO;

	@Autowired
	SensitiveService sensitiveService;

	public int addMessage(Message message) {
		message.setContent(HtmlUtils.htmlEscape(message.getContent()));
		message.setContent(sensitiveService.filter(message.getContent()));
		return messageDAO.addMessage(message) > 0 ? message.getId() : 0;
	}

	public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
		return messageDAO.getConversationDetail(conversationId, offset, limit);
	}

	public List<Message> getConversationList(int userId, int offset, int limit) {
		return messageDAO.getConversationList(userId, offset, limit);
	}

	public int getConversationUnreadCount(int userId, String conversationId) {
		return messageDAO.getConversationUnreadCount(userId, conversationId);
	}

	public void updateHasRead(int userId, String conversationId) {
		messageDAO.updateHasRead(userId, conversationId);
	}
}
