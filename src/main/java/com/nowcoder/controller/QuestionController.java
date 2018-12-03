package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

@Controller
public class QuestionController {
	private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

	@Autowired
	QuestionService questionService;

	@Autowired
	CommentService commentService;

	@Autowired
	UserService userService;

	@Autowired
	LikeService likeService;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	FollowService followService;

	@RequestMapping(path = { "/question/add" }, method = RequestMethod.POST)
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content) {
		try {
			Question question = new Question();
			if (hostHolder.getUser() == null) {
				return WendaUtil.getJSONString(999);
			}
			question.setUserId(hostHolder.getUser().getId());
			question.setTitle(title);
			question.setContent(content);
			question.setCreatedDate(new Date());
			question.setCommentCount(0);
			if (questionService.addQuestion(question) > 0) {
				return WendaUtil.getJSONString(0);
			}
		} catch (Exception e) {
			logger.error("添加题目失败" + e.getMessage());
		}
		return WendaUtil.getJSONString(1, "失败");
	}

	@RequestMapping(path = { "/question/{qid}" }, method = RequestMethod.GET)
	public String questionDetail(Model model, @PathVariable("qid") int qid) {
		Question question = questionService.getById(qid);
		model.addAttribute("question", question);

		List<Comment> commentlist = commentService.getCommentByEntity(EntityType.ENTITY_QUESTION, qid);
		List<ViewObject> comments = new ArrayList<>();
		for (Comment comment : commentlist) {
			ViewObject vo = new ViewObject();
			vo.set("comment", comment);
			vo.set("user", userService.getUser(comment.getUserId()));
			if (hostHolder.getUser() == null) {
				vo.set("liked", 0);
			} else {
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,
						comment.getId()));
			}
			vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			comments.add(vo);
		}
		model.addAttribute("comments", comments);

		List<ViewObject> followUsers = new ArrayList<ViewObject>();
		// 获取关注的用户信息
		List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 0, 20);
		for (Integer userId : users) {
			ViewObject vo = new ViewObject();
			User u = userService.getUser(userId);
			if (u == null) {
				continue;
			}
			vo.set("name", u.getName());
			vo.set("headUrl", u.getHeadUrl());
			vo.set("id", u.getId());
			followUsers.add(vo);
		}
		model.addAttribute("followUsers", followUsers);
		if (hostHolder.getUser() != null) {
			model.addAttribute("followed",
					followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
		} else {
			model.addAttribute("followed", false);
		}
		return "detail";
	}
}
