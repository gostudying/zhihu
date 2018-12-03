package com.nowcoder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.WendaUtil;

@Controller
public class LikeController {
	@Autowired
	LikeService likeService;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	CommentService commentService;

	@Autowired
	EventProducer eventProducer;

	@RequestMapping(path = { "/like" })
	@ResponseBody
	public String like(Model model, @RequestParam("commentId") int commentId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}

		// 点赞之后将异步事件发送出去
		EventModel eventModel = new EventModel();
		eventModel.setType(EventType.LIKE);
		eventModel.setActorId(hostHolder.getUser().getId());
		eventModel.setEntityType(EntityType.ENTITY_COMMENT);
		eventModel.setEntityId(commentId);
		eventModel.setEntityOwnerId(commentService.getCommentById(commentId).getUserId());
		eventModel.setExt("questionId", String.valueOf(commentService.getCommentById(commentId).getEntityId()));
		eventProducer.fireEvent(eventModel);

		long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0, String.valueOf(likeCount));
	}

	@RequestMapping(path = { "/dislike" })
	@ResponseBody
	public String dilike(Model model, @RequestParam("commentId") int commentId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}
		long likeCount = likeService.dislike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0, String.valueOf(likeCount));
	}
}
