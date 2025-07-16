package com.java_web.backend.Common.Mapper;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_web.backend.Common.Entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 保留原有的函数
    @Select("select * from user where id = #{id}")
    User selectById(Integer id);
    
    // 添加新的关联查询函数
    @Results({
        @Result(id = true, property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "courses", column = "id", 
                many = @Many(select = "com.java_web.backend.Mapper.CourseMapper.selectCoursesByTeacher"))
    })
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserWithCourses(Integer id);

    @Select("select * from user where email = #{email} and is_deleted = 0")
    User findByEmail(String email);
    
    @Select("select * from user where username = #{username} and is_deleted = 0")
    User findByUsername(String username);
}
