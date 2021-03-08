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

    /**
     * @param maps
     * @return
     */
    int insertFormData(@Param("maps") List<HashMap<String, Object>> maps);

    List<HashMap<String, String>> selectFormData(@Param("PROC_INST_ID_") String PROC_INST_ID_);

}
