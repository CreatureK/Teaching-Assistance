package com.java_web.backend.Common.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_web.backend.Common.Entity.CourseObjective;

@Mapper
public interface CourseObjectMapper extends BaseMapper<CourseObjective> {
    // 保留原有的函数
    @Select("select * from course_objective where course_id = #{courseId}")
    CourseObjective selectObjectiveById(@Param("courseId") Integer courseId);
    
    // 添加新的关联查询函数
    @Results({
        @Result(id = true, property = "courseId", column = "course_id"),
        @Result(property = "courseContent", column = "course_content"),
        @Result(property = "teachingTarget", column = "teaching_target"),
        @Result(property = "course", column = "course_id", 
                one = @One(select = "com.java_web.backend.Mapper.CourseMapper.selectById"))
    })
    @Select("SELECT * FROM course_objective WHERE course_id = #{courseId}")
    CourseObjective getObjectiveWithCourse(Integer courseId);
}
