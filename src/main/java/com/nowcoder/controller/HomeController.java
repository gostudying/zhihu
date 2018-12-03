package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;

@Controller
public class HomeController {

	@Autowired
	UserService userService;

	@Autowired
	QuestionService questionService;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	CommentService commentService;

	@Autowired
	FollowService followService;

	@RequestMapping(path = { "/index", "/" }, method = RequestMethod.GET)
	public String index(Model model) {
		List<ViewObject> vos = getQuestions(0, 0, 10);
		model.addAttribute("vos", vos);
		return "index";
	}

	@RequestMapping(path = { "/user/{userId}" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String userIndex(Model model, @PathVariable("userId") int userId) {
		model.addAttribute("vos", getQuestions(userId, 0, 10));

		User user = userService.getUser(userId);
		ViewObject vo = new ViewObject();
		vo.set("user", user);
		vo.set("commentCount", commentService.getUserCommentCount(userId));
		vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
		vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
		if (hostHolder.getUser() != null) {
			vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
		} else {
			vo.set("followed", false);
		}
		model.addAttribute("profileUser", vo);
		return "profile";
	}
	// @RequestMapping(path = { "/user/{userId}" }, method = RequestMethod.GET)
	// public String userIndex(Model model, @PathVariable("userId") int userId)
	// {
	// List<ViewObject> vos = getQuestions(userId, 0, 10);
	// model.addAttribute("vos", vos);
	// return "index";
	// }

	public List<ViewObject> getQuestions(int userId, int offset, int limit) {
		List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
		List<ViewObject> vos = new ArrayList<>();
		for (Question question : questionList) {
			ViewObject vo = new ViewObject();
			vo.set("question", question);
			vo.set("user", userService.getUser(question.getUserId()));
			vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
			vos.add(vo);
		}
		return vos;
	}
}
