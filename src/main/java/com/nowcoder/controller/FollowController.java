package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

@Controller
public class FollowController {
	@Autowired
	CommentService commentService;

	@Autowired
	QuestionService questionService;

	@Autowired
	FollowService followService;

	@Autowired
	EventProducer eventProducer;

	@Autowired
	UserService userService;

	@Autowired
	HostHolder hostHolder;

	@RequestMapping(path = { "/followUser" }, method = RequestMethod.POST)
	@ResponseBody
	public String followUser(@RequestParam("userId") int userId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}
		boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
		// 异步发送关注消息
		EventModel eventModel = new EventModel();
		eventModel.setType(EventType.FOLLOW);
		eventModel.setActorId(hostHolder.getUser().getId());
		eventModel.setEntityId(userId);
		eventModel.setEntityType(EntityType.ENTITY_USER);
		eventModel.setEntityOwnerId(userId);
		eventProducer.fireEvent(eventModel);

		// 返回host关注了多少个用户
		return WendaUtil.getJSONString(ret ? 0 : 1,
				String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
	}

	@RequestMapping(path = { "/unfollowUser" }, method = RequestMethod.POST)
	@ResponseBody
	public String unfollowUser(@RequestParam("userId") int userId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}
		boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

		EventModel eventModel = new EventModel();
		eventModel.setType(EventType.UNFOLLOW);
		eventModel.setActorId(hostHolder.getUser().getId());
		eventModel.setEntityId(userId);
		eventModel.setEntityType(EntityType.ENTITY_USER);
		eventModel.setEntityOwnerId(userId);
		eventProducer.fireEvent(eventModel);

		return WendaUtil.getJSONString(ret ? 0 : 1,
				String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
	}

	@RequestMapping(path = { "/followQuestion" }, method = RequestMethod.POST)
	@ResponseBody
	public String followQuestion(@RequestParam("questionId") int questionId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}

		Question question = questionService.getById(questionId);
		if (question == null) {
			return WendaUtil.getJSONString(1, "问题不存在");
		}
		boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

		EventModel eventModel = new EventModel();
		eventModel.setType(EventType.FOLLOW);
		eventModel.setActorId(hostHolder.getUser().getId());
		eventModel.setEntityId(questionId);
		eventModel.setEntityType(EntityType.ENTITY_QUESTION);
		eventModel.setEntityOwnerId(question.getUserId());
		eventProducer.fireEvent(eventModel);

		Map<String, Object> info = new HashMap<>();
		info.put("headUrl", hostHolder.getUser().getHeadUrl());
		info.put("name", hostHolder.getUser().getName());
		info.put("id", hostHolder.getUser().getId());
		info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
		return WendaUtil.getJSONString(ret ? 0 : 1, info);
	}

	@RequestMapping(path = { "/unfollowQuestion" }, method = RequestMethod.POST)
	@ResponseBody
	public String unfollowQuestion(@RequestParam("questionId") int questionId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}
		Question question = questionService.getById(questionId);
		if (question == null) {
			return WendaUtil.getJSONString(1, "问题不存在");
		}
		boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

		EventModel eventModel = new EventModel();
		eventModel.setType(EventType.UNFOLLOW);
		eventModel.setActorId(hostHolder.getUser().getId());
		eventModel.setEntityId(questionId);
		eventModel.setEntityType(EntityType.ENTITY_QUESTION);
		eventModel.setEntityOwnerId(question.getUserId());
		eventProducer.fireEvent(eventModel);

		Map<String, Object> info = new HashMap<>();
		info.put("headUrl", hostHolder.getUser().getHeadUrl());
		info.put("name", hostHolder.getUser().getName());
		info.put("id", hostHolder.getUser().getId());
		info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
		return WendaUtil.getJSONString(ret ? 0 : 1, info);
	}

	@RequestMapping(path = { "/user/{uid}/followees" }, method = RequestMethod.GET)
	public String followees(Model model, @PathVariable("uid") int userId) {
		// 获取该user的关注用户列表
		List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
		if (hostHolder.getUser() != null) {
			model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
		} else {
			model.addAttribute("followees", getUsersInfo(0, followeeIds));
		}
		
		model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
		model.addAttribute("curUser", userService.getUser(userId));
		return "followees";
	}

	@RequestMapping(path = { "/user/{uid}/followers" }, method = RequestMethod.GET)
	public String followers(Model model, @PathVariable("uid") int userId) {
		// 获取该用户的粉丝列表
		List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
		if (hostHolder.getUser() != null) {
			model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
		} else {
			model.addAttribute("followers", getUsersInfo(0, followerIds));
		}
		model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
		model.addAttribute("curUser", userService.getUser(userId));
		return "followers";
	}

	private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
		List<ViewObject> userInfos = new ArrayList<>();
		for (Integer uid : userIds) {
			User user = userService.getUser(uid);
			if (user == null) {
				continue;
			}

			ViewObject vo = new ViewObject();
			vo.set("user", user);
			vo.set("commentCount", commentService.getUserCommentCount(uid));
			vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
			vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, uid));
			if (localUserId != 0) {
				vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
			} else {
				vo.set("followed", false);
			}
			userInfos.add(vo);
		}
		return userInfos;
	}
}
