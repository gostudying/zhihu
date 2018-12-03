package com.nowcoder.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.Message;

@Mapper
public interface MessageDAO {
	String TABLE_NAME = " message ";
	String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;

	@Insert({ "insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") ",
			"values(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})" })
	int addMessage(Message message);

	@Select({ "select ", SELECT_FIELDS, " from ", TABLE_NAME,
			" where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}" })
	List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset,
			@Param("limit") int limit);

	@Select("SELECT from_id, to_id, content, created_date, has_read, conversation_id, COUNT(*) id FROM (SELECT * FROM message WHERE from_id =#{userId} or to_id=#{userId} ORDER BY created_date DESC) tt GROUP BY conversation_id ORDER BY created_date DESC limit #{offset},#{limit}")
	List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset,
			@Param("limit") int limit);

	@Select({ "select count(id) from ", TABLE_NAME,
			" where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}" })
	int getConversationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

	@Update({ "update ", TABLE_NAME,
			" set has_read=1 where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}" })
	void updateHasRead(@Param("userId") int userId, @Param("conversationId") String conversationId);
}
