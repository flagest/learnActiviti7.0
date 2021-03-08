package com.imooc.activitiweb.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author jzwu
 * @since 2021/3/7 0007
 */
@Mapper
@Component
public interface FormDataMapper {

    int insertFormData(@Param("maps") List<HashMap<String, Object>> maps);

}
