package com.nowcoder.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JedisAdapter implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
	private JedisPool jedisPool;

	@Override
	public void afterPropertiesSet() throws Exception {
		jedisPool = new JedisPool("redis://localhost:6379/10");
	}

	public long sadd(String key, String members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sadd(key, members);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}

	public long srem(String key, String members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srem(key, members);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}

	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}

	public boolean sismember(String key, String members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key, members);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	public long lpush(String key, String members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpush(key, members);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}

	public List<String> brpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, key);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	public Jedis getJedis() {
		return jedisPool.getResource();
	}

	public Transaction multi(Jedis jedis) {
		try {
			return jedis.multi();
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
		}
		return null;
	}

	public List<Object> exec(Transaction tx, Jedis jedis) {
		try {
			return tx.exec();
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
		} finally {
			if (tx != null) {
				try {
					tx.close();
				} catch (IOException e) {
					logger.error("发生异常" + e.getMessage());
				}
			}
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	public long zadd(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}

	public Set<String> zrevrange(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	public long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcard(key);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}

	public Double zscore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key, member);
		} catch (Exception e) {
			logger.error("redis异常" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}
}
