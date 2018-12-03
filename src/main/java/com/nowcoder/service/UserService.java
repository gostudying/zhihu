package com.nowcoder.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;

@Service
public class UserService {
	@Autowired
	UserDAO userDao;

	@Autowired
	LoginTicketDAO loginTicketDAO;

	public User getUser(int id) {
		return userDao.selectById(id);
	}

	public User getUserByName(String name) {
		return userDao.selectByName(name);
	}

	public Map<String, String> register(String username, String password) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(username)) {
			map.put("msg", "用户名不能为空");
			return map;
		}
		if (StringUtils.isBlank(username)) {
			map.put("msg", "密码不能为空");
			return map;
		}
		User user = userDao.selectByName(username);
		if (user != null) {
			map.put("msg", "用户名已存在");
			return map;
		}

		user = new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0, 5));
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		user.setPassword(WendaUtil.MD5(password + user.getSalt()));
		userDao.addUser(user);

		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}

	public Map<String, String> login(String username, String password) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(username)) {
			map.put("msg", "用户名不能为空");
			return map;
		}
		if (StringUtils.isBlank(username)) {
			map.put("msg", "密码不能为空");
			return map;
		}
		User user = userDao.selectByName(username);
		if (user == null) {
			map.put("msg", "用户名不存在");
			return map;
		}
		if (!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
			map.put("msg", "密码错误");
			return map;
		}
		// 登录成功，给用户一个ticket
		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}

	public String addLoginTicket(int userId) {
		LoginTicket loginTicket = new LoginTicket();
		loginTicket.setUserId(userId);
		loginTicket.setStatus(0);
		Date now = new Date();
		now.setTime(3600 * 24 * 100 + now.getTime());
		loginTicket.setExpired(now);
		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
		loginTicketDAO.addTicket(loginTicket);
		return loginTicket.getTicket();
	}

	public void logout(String ticket) {
		loginTicketDAO.updateStatus(ticket, 1);
	}
}
