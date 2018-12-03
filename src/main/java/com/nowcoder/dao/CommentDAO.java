package com.nowcoder.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.Comment;

@Mapper
public interface CommentDAO {
	String TABLE_NAME = " comment ";
	String INSERT_FIELDS = " user_id, entity_id, entity_type, content, created_date, status ";
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;

	@Insert({ "insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") ",
			"values(#{userId},#{entityId},#{entityType},#{content},#{createdDate},#{status})" })
	int addComment(Comment comment);

	@Select({ "select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}" })
	Comment getCommentById(int id);

	@Select({ "select ", SELECT_FIELDS, " from ", TABLE_NAME,
			" where entity_type=#{entityType} and entity_id=#{entityId} order by created_date desc" })
	List<Comment> selectCommentByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

	@Select({ "select count(id) from ", TABLE_NAME, " where entity_type=#{entityType} and entity_id=#{entityId}" })
	int getCommentCount(@Param("entityType") int entityType, @Param("entityId") int entityId);

	@Update({ "update ", TABLE_NAME, " set status=#{status} where commentId=#{commentId}" })
	int updateStatus(@Param("commentId") int commentId, @Param("status") int status);

	@Select({ "select count(id) from ", TABLE_NAME, " where user_id=#{userId} and status=0" })
	int getUserCommentCount(int userId);
}
