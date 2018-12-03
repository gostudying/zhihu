package com.nowcoder.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;

@Component
public class PassportInterceptor implements HandlerInterceptor {
	@Autowired
	LoginTicketDAO loginTicketDAO;

	@Autowired
	UserDAO userDAO;

	@Autowired
	HostHolder hostHolder;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// 每一个线程都要重新判断一遍ticket，所以请求结束后要清空
		hostHolder.clear();
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2,
			ModelAndView modelAndView) throws Exception {
		// 模板渲染前
		if (modelAndView != null) {
			modelAndView.addObject("user", hostHolder.getUser());
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		String ticket = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("ticket")) {
					ticket = cookie.getValue();
					break;
				}
			}
		}
		if (ticket != null) {
			LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
			// 无效ticket
			if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
				return true;
			}
			User user = userDAO.selectById(loginTicket.getUserId());
			hostHolder.setUser(user);
		}
		return true;
	}

}
