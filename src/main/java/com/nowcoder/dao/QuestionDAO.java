package com.nowcoder.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.Question;

@Mapper
public interface QuestionDAO {
	String TABLE_NAME = " question ";
	String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;

	@Insert({ "insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") ",
			"values(#{title},#{content},#{createdDate},#{userId},#{commentCount})" })
	int addQuestion(Question question);

	List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
			@Param("limit") int limit);

	@Select({ "select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}" })
	Question selectById(int id);

	@Update({ "update ", TABLE_NAME, " set comment_count=#{comment_count} where id = #{id}" })
	void updateCommentCount(@Param("id") int id, @Param("comment_count") int comment_count);

}
