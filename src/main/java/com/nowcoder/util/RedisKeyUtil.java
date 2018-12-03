package com.nowcoder.util;

public class RedisKeyUtil {
	private static String SPLIT = ":";
	private static String BIZ_LIKE = "LIKE";
	private static String BIZ_DISLIKE = "DISLIKE";
	private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
	// 粉丝
	private static String BIZ_FOLLOWER = "FOLLOWER";
	// 关注对象
	private static String BIZ_FOLLOWEE = "FOLLOWEE";

	public static String getLikeKey(int entityType, int entityId) {
		return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
	}

	public static String getdislikeKey(int entityType, int entityId) {
		return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
	}

	// 每个实体都有一个follower key，可以把userId存入其中，表示该实体的粉丝有这个user
	public static String getFollowerKey(int entityType, int entityId) {
		return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
	}

	// 每个用户对每个entityType都有一个followee Key，可以把entityId存入其中，表示该用户对这个类型的实体进行了关注
	public static String getFolloweeKey(int userId, int entityType) {
		return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
	}

	public static String getEventQueueKey() {
		return BIZ_EVENTQUEUE;
	}
}
