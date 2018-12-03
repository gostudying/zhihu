package com.nowcoder.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.apache.ibatis.io.Resources;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

//前缀树结点
class TrieNode {
	// 是不是关键词的结尾，默认不是
	private boolean end = false;

	// 当前结点下所有子结点
	private Map<Character, TrieNode> subNodes = new HashMap<>();

	void addSubNode(char key, TrieNode node) {
		subNodes.put(key, node);
	}

	TrieNode getSubNode(char key) {
		return subNodes.get(key);
	}

	void setEnd(boolean end) {
		this.end = end;
	}

	boolean isEnd() {
		return end;
	}
}

@Service
public class SensitiveService implements InitializingBean {
	private TrieNode root = new TrieNode();

	@Override
	public void afterPropertiesSet() throws Exception {
		InputStream is = Resources.getResourceAsStream("SensitiveWords.txt");
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String lineText = null;
		// 一行一行读取文件内容,并加入前缀树中
		while ((lineText = bufferedReader.readLine()) != null) {
			addWord(lineText.trim());
		}
		bufferedReader.close();
	}

	// 增加关键词
	void addWord(String word) {
		TrieNode cur = root;
		for (int i = 0; i < word.length(); ++i) {
			char c = word.charAt(i);
			TrieNode node = cur.getSubNode(c);
			// 如果前缀树中没有该结点，则增加
			if (node == null) {
				node = new TrieNode();
				cur.addSubNode(c, node);
			}
			cur = node;
			// 结尾end设为true
			if (i == word.length() - 1) {
				cur.setEnd(true);
			}
		}
	}

	// 过滤文本
	public String filter(String text) {
		StringBuilder result = new StringBuilder();
		String replacement = "**";
		TrieNode cur = root;// 指向前缀树
		int begin = 0;
		int index = 0;
		while (index < text.length()) {
			char c = text.charAt(index);
			// 如果是符号，则跳过，避免用户发“傻@逼”
			if (isSymbol(c)) {
				if (cur == root) {
					result.append(c);
					begin++;
				}
				index++;
				continue;
			}
			cur = cur.getSubNode(c);
			if (cur == null) {
				result.append(text.charAt(begin));
				begin++;
				index = begin;
				cur = root;
			} else if (cur.isEnd()) {
				result.append(replacement);
				index++;
				begin = index;
				cur = root;
			} else {
				index++;
			}
		}
		return result.toString();
	}

	// 将非英文，非东亚文字设置为false,即非法字符
	private boolean isSymbol(char c) {
		int ic = (int) c;
		return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
	}

}
