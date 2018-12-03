package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;

@Service
public class CommentService {
	@Autowired
	CommentDAO commentDAO;

	@Autowired
	SensitiveService sensitiveService;

	public int addComment(Comment comment) {
		comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
		comment.setContent(sensitiveService.filter(comment.getContent()));
		return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
	}

	public Comment getCommentById(int id) {
		return commentDAO.getCommentById(id);
	}

	public List<Comment> getCommentByEntity(int entityType, int entityId) {
		return commentDAO.selectCommentByEntity(entityType, entityId);
	}

	public int getCommentCount(int entityType, int entityId) {
		return commentDAO.getCommentCount(entityType, entityId);
	}

	public boolean deleteComment(int commentId) {
		return commentDAO.updateStatus(commentId, 1) > 0;
	}

	public int getUserCommentCount(int userId) {
		return commentDAO.getUserCommentCount(userId);
	}
}
