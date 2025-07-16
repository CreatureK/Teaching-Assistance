package com.java_web.backend.Common.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_web.backend.Common.Entity.Restriction;

@Mapper
public interface RestrictionMapper extends BaseMapper<Restriction> {
    // 保留原有的函数
    @Select("select * from restriction where user_id = #{userId}")
    List<Restriction> selectByUserId(Integer userId);
    
    @Select("select * from restriction where user_id = #{userId} and function_name = #{functionName}")
    Restriction selectByUserIdAndFunction(Integer userId, String functionName);
    
    // 添加新的关联查询函数
    @Results({
        @Result(id = true, property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "functionName", column = "function_name"),
        @Result(property = "user", column = "user_id", 
                one = @One(select = "com.java_web.backend.Mapper.UserMapper.selectById"))
    })
    @Select("SELECT * FROM restriction WHERE id = #{id}")
    Restriction getRestrictionWithUser(Integer id);
}
