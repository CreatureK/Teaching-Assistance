package com.java_web.backend.Common.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_web.backend.Common.Entity.Material;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    // 保留原有的函数
    @Select("select * from material where course_id = #{courseId}")
    Material selectByCourseId(Integer courseId);
    
    // 添加新的关联查询函数
    @Results({
        @Result(id = true, property = "courseId", column = "course_id"),
        @Result(property = "content", column = "content"),
        @Result(property = "course", column = "course_id", 
                one = @One(select = "com.java_web.backend.Mapper.CourseMapper.selectById"))
    })
    @Select("SELECT * FROM material WHERE course_id = #{courseId}")
    Material getMaterialWithCourse(Integer courseId);
}
