package com.nowcoder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@Service
public class FollowService {
	@Autowired
	JedisAdapter jedisAdapter;

	// 用户关注一个实体，成功返回true
	public boolean follow(int userId, int entityType, int entityId) {
		// 实体粉丝列表
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		// 用户关注列表
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		// 关注时间
		Date date = new Date();
		Jedis jedis = jedisAdapter.getJedis();
		// 开启事物
		Transaction tx = jedisAdapter.multi(jedis);
		// 一个用户关注了一个实体，需要将该用户id添加到这个实体的粉丝列表中
		tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
		// 一个用户关注了一个实体，需要将该实体id添加到这个用户的关注列表中
		tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
		// 执行事务，返回之前添加的每个事件的返回结果
		List<Object> ret = jedisAdapter.exec(tx, jedis);
		return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
	}

	public boolean unfollow(int userId, int entityType, int entityId) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		Jedis jedis = jedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		// 一个用户取消关注了一个实体，需要将该用户id从该实体粉丝列表中删除
		tx.zrem(followerKey, String.valueOf(userId));
		// 一个用户取消关注了一个实体，需要将该实体id从该用户关注列表中删除
		tx.zrem(followeeKey, String.valueOf(entityId));
		List<Object> ret = jedisAdapter.exec(tx, jedis);
		return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
	}

	private List<Integer> getIdsFromSet(Set<String> idset) {
		List<Integer> ids = new ArrayList<>();
		for (String str : idset) {
			ids.add(Integer.parseInt(str));
		}
		return ids;
	}

	// 获取每个实体的粉丝id列表
	public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));
	}

	// 获取每个用户的关注列表
	public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
	}

	// 获取一个实体的粉丝数量
	public long getFollowerCount(int entityType, int entityId) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zcard(followerKey);
	}

	// 获取一个用户的关注数量
	public long getFolloweeCount(int userId, int entityType) {
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		return jedisAdapter.zcard(followeeKey);
	}

	// 判断一个用户是否是一个实体的粉丝
	public boolean isFollower(int userId, int entityType, int entityId) {
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
	}
}
