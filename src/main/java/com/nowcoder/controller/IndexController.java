package com.nowcoder.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.model.User;

//@Controller
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping(path = { "/index", "/" })
	@ResponseBody
	public String index(HttpSession httpSession) {
		logger.info("Hello Nowcoder");
		return "Hello Nowcoder " + httpSession.getAttribute("msg");
	}

	@RequestMapping(path = { "/profile/{userId}" })
	@ResponseBody
	public String profile(@PathVariable("userId") int userId,
			@RequestParam(value = "type", defaultValue = "121", required = false) String type) {
		return "profile page of " + userId + ":" + type;
	}

	@RequestMapping(path = "/vm", method = { RequestMethod.GET })
	public String template(Model model) {
		model.addAttribute("value1", "hello1");

		List<String> colors = Arrays.asList(new String[] { "red", "green", "blue" });
		model.addAttribute("colors", colors);

		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 4; ++i) {
			map.put(String.valueOf(i), String.valueOf(i * i));
		}
		model.addAttribute("map", map);

		model.addAttribute("user", new User("Lee"));
		return "home";
	}

	@RequestMapping(path = "/redirect/{code}", method = { RequestMethod.GET })
	public String redirect(Model model, HttpSession httpSession) {
		httpSession.setAttribute("msg", "jump from redirect");
		return "redirect:/";
	}

	@RequestMapping(path = { "/admin" })
	@ResponseBody
	public String admin(@RequestParam("key") String key) {
		if (key.equals("admin")) {
			return "hello admin";
		}
		throw new IllegalArgumentException("参数不对");
	}

	@ExceptionHandler
	@ResponseBody
	public String error(Exception ex) {
		return "error:" + ex.getMessage();
	}
}
