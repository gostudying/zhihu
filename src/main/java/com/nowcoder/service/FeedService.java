package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.FeedDAO;
import com.nowcoder.model.Feed;

@Service
public class FeedService {
	@Autowired
	FeedDAO feedDAO;

	// 拉模式
	public List<Feed> selectUserFeeds(int maxId, List<Integer> userIds, int count) {
		return feedDAO.selectUserFeeds(maxId, userIds, count);
	}

	public boolean addFeed(Feed feed) {
		feedDAO.addFeed(feed);
		return feed.getId() > 0;
	}

	// 推模式
	public Feed getById(int id) {
		return feedDAO.getFeedById(id);
	}
}
