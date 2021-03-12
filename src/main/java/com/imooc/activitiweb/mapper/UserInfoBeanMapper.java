package com.imooc.activitiweb.mapper;


import com.imooc.activitiweb.pojo.UserInfoBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author jzwu
 * @since 2021/3/1 0001
 */
@Mapper
@Component
public interface UserInfoBeanMapper {
    // 通过genor
    UserInfoBean selectByUsername(@Param("username") String username);

    List<Map<String, Object>> selectUser();
}