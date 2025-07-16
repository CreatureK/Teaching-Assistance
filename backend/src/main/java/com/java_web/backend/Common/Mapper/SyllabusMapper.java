package com.java_web.backend.Common.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_web.backend.Common.Entity.Syllabus;

@Mapper
public interface SyllabusMapper extends BaseMapper<Syllabus> {
    // 保留原有的函数
    @Select("select * from syllabus where course_id = #{courseId}")
    Syllabus getSyllabusByCourseId(Integer courseId);
    
    // 添加新的关联查询函数
    @Results({
        @Result(id = true, property = "courseId", column = "course_id"),
        @Result(property = "content", column = "content"),
        @Result(property = "course", column = "course_id", 
                one = @One(select = "com.java_web.backend.Mapper.CourseMapper.selectById"))
    })
    @Select("SELECT * FROM syllabus WHERE course_id = #{courseId}")
    Syllabus getSyllabusWithCourse(Integer courseId);
}
