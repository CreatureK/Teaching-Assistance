package com.java_web.backend.Common.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_web.backend.Common.Entity.Course;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    // 保留原有的函数
    @Select("select * from Course where id = #{id}")
    Course selectById(Integer courseId);

    @Select("SELECT * FROM course WHERE teacher_id = #{teacherId} AND is_deleted = 0")
    List<Course> selectCoursesByTeacher(Integer teacherId);
    
    // 添加新的关联查询函数
    @Results({
        @Result(id = true, property = "id", column = "id"),
        @Result(property = "teacherId", column = "teacher_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "status", column = "status"),
        @Result(property = "isDeleted", column = "is_deleted"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "teacher", column = "teacher_id", 
                one = @One(select = "com.java_web.backend.Mapper.UserMapper.selectById"))
    })
    
    @Select("SELECT * FROM course WHERE id = #{id}")
    Course getCourseWithTeacher(Integer id);
}
