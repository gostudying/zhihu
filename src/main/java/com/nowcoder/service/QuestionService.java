package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;

@Service
public class QuestionService {
	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	SensitiveService sensitiveService;

	public List<Question> getLatestQuestions(int userId, int offset, int limit) {
		return questionDAO.selectLatestQuestions(userId, offset, limit);
	}

	public int addQuestion(Question question) {
		// 脚本过滤
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		// 敏感词过滤
		question.setTitle(sensitiveService.filter(question.getTitle()));
		question.setContent(sensitiveService.filter(question.getContent()));
		return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
	}
	
	public Question getById(int qid){
		return questionDAO.selectById(qid);
	}
	
	public void updateCommentCount(int qid, int count){
		questionDAO.updateCommentCount(qid, count);
	}
}
